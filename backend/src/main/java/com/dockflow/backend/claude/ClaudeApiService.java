package com.dockflow.backend.claude;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ClaudeApiService {

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model}")
    private String model;

    @Value("${claude.api.max-tokens}")
    private int maxTokens;

    @Value("${claude.api.temperature}")
    private double temperature;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();


    /* 문서 요약 + 태그 생성 (한 번의 API 호출로 처리) */
    public SummaryResult summarizeDocument(String documentText, String documentTitle) {
        String prompt = createSummaryPrompt(documentText, documentTitle);

        try {
            String response = callClaudeApi(prompt);
            return parseSummaryResponse(response);
        } catch (IOException e) {
            log.error("Claude API 호출 실패", e);
            throw new RuntimeException("AI 요약 생성에 실패했습니다.", e);
        }
    }

    /* 요약 프롬프트 생성 */
    private String createSummaryPrompt(String documentText, String documentTitle) {
        return String.format("""
            다음 문서를 요약하고 주요 태그를 추출해주세요.
            
            문서 제목: %s
            
            문서 내용:
            %s
            
            요구사항:
            1. **핵심 요약**: 문서의 주요 목적이나 결론을 1-2문장으로 작성
            2. **주요 내용**: 핵심 포인트를 불릿 포인트로 3-5개 나열
            3. **세부 사항**: 필요시 추가 설명을 1-2문장으로 작성
            4. **태그**: 문서의 주요 키워드 3-5개 추출
            
            응답 형식 (반드시 JSON 형식으로, 줄바꿈은 \\n 사용):
            {
                "summary": "핵심 요약 문장.\\n\\n• 주요 내용 1\\n• 주요 내용 2\\n• 주요 내용 3\\n\\n세부 설명 문단.",
                "tags": ["태그1", "태그2", "태그3"]
            }
            
            예시:
            {
                "summary": "본 문서는 2024년 마케팅 전략 수립을 위한 시장 분석 보고서입니다.\\n\\n• 타겟 고객층: 25-35세 직장인\\n• 주요 채널: 인스타그램, 유튜브\\n• 예상 ROI: 150%%\\n• 캠페인 기간: 3개월\\n\\n경쟁사 대비 차별화된 콘텐츠 전략으로 브랜드 인지도 향상을 목표로 합니다.",
                "tags": ["마케팅전략", "시장분석", "소셜미디어", "ROI"]
            }
            """, documentTitle, documentText);

    }

    /* Claude API 호출 */
   private String callClaudeApi(String prompt) throws IOException {
       JsonObject requestBody = new JsonObject();
       requestBody.addProperty("model", model);
       requestBody.addProperty("max_tokens", maxTokens);
       requestBody.addProperty("temperature", temperature);

       JsonObject userMessage = new JsonObject();
       userMessage.addProperty("role", "user");
       userMessage.addProperty("content", prompt);

       com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
       messages.add(userMessage);
       requestBody.add("messages", messages);

       String requestBodyStr = requestBody.toString();
       log.info("=== Claude API 요청 시작 ===");
       log.info("URL: https://api.anthropic.com/v1/messages");
       log.debug("요청 본문: {}", requestBodyStr);

       RequestBody body = RequestBody.create(
               requestBody.toString(),
               MediaType.parse("application/json")
       );

       Request request = new Request.Builder()
               .url("https://api.anthropic.com/v1/messages")
               .addHeader("x-api-key", apiKey)
               .addHeader("anthropic-version", "2023-06-01")
               .addHeader("content-type", "application/json")
               .post(body)
               .build();

       try (Response response = httpClient.newCall(request).execute()) {

           String responseBody = "";
           if (response.body() != null) {
               responseBody = response.body().string();
           }

           log.info("=== Claude API 응답 ===");
           log.info("상태 코드: {}", response.code());
           log.debug("응답 본문: {}", responseBody);


           if (!response.isSuccessful()) {
               log.error("Claude API 오류 발생!");
               throw new IOException("API 호출 실패: " + response.code() + " - " + responseBody);
            }
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

           if (!jsonResponse.has("content")) {
               log.error("응답에 content 필드가 없습니다: {}", responseBody);
               throw new IOException("잘못된 API 응답 형식");
           }

            return jsonResponse
                    .getAsJsonArray("content")
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString();
       }
   }

   /* 응답 파싱 */
   private SummaryResult parseSummaryResponse(String response)
   {
       log.debug("파싱할 응답: {}", response);

       try {
           String cleanedResponse = response
                   .replaceAll("```json\\s*", "")
                   .replaceAll("```\\s*", "")
                   .trim();

           log.debug("정제된 응답: {}", cleanedResponse);

           JsonObject json = gson.fromJson(cleanedResponse, JsonObject.class);

           String summary = json.get("summary").getAsString();

           List<String> tags = new ArrayList<>();
           json.getAsJsonArray("tags").forEach(tag -> tags.add(tag.getAsString()));

           log.info("파싱 완료 - 요약: {}자, 태그: {}개", summary.length(), tags.size());
           return new SummaryResult(summary, tags);
       } catch (Exception e) {
           log.error("응답 파싱 실패: {}", response, e);
           throw new RuntimeException("AI 응답 처리에 실패했습니다.", e);
       }
   }

   /* 요약 결과 DTO */
   public record SummaryResult(String summary, List<String> tags) {};
}
