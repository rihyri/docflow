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
                1. 문서의 핵심 내용을 자연스러운 문단 형식으로 요약해주세요 (200-400자 정도)
                2. 문서의 주요 키워드 3-5개를 추출해주세요
                
                응답 형식 (반드시 JSON 형식으로):
                {
                    "summary": "문서 요약 내용",
                    "tags": ["태그1", "태그2", "태그3"]
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
