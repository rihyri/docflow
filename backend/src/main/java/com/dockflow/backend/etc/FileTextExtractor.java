package com.dockflow.backend.etc;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class FileTextExtractor {

    /* 파일에서 텍스트 추출 */
    public String extractText(String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".pdf")) {
            return extractFromPdf(file);
        } else if (fileName.endsWith(".docx")) {
            return extractFromDocx(file);
        } else if (fileName.endsWith(".doc")) {
            return extractFromDoc(file);
        } else if (fileName.endsWith(".txt")) {
            return extractFromTxt(file);
        }

        throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. : " + fileName);
    }

    /* PDF 텍스트 추출 */
    private String extractFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            return cleanText(text);
        }
    }

    /* DOCX 텍스트 추출 */
    private String extractFromDocx(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            StringBuilder text = new StringBuilder();
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph para : paragraphs) {
                text.append(para.getText()).append("\n");
            }

            return cleanText(text.toString());
        }
    }

    /* DOC 텍스트 추출 */
    private String extractFromDoc(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {

            String text = extractor.getText();
            return cleanText(text);
        }
    }

    /* TXT 텍스트 추출 */
    private String extractFromTxt(File file) throws IOException {
        return cleanText(new String(java.nio.file.Files.readAllBytes(file.toPath()), "UTF-8"));
    }

    /* 텍스트 정리 */
    private String cleanText(String text) {

        if (text == null) return "";

        // 연속된 공백 하나로
        text = text.replaceAll("[ \\t]+", " ");

        // 연속된 줄바꿈 2개로 제한
        text = text.replaceAll("\\n{3,}", "\n\n");

        return text.trim();
    }

    /* 텍스트 길이 제한 (토큰 절약) */
    public String limitTextLength(String text, int maxChars) {
        if (text.length() <= maxChars) {
            return text;
        }

        log.warn("텍스트가 너무 깁니다. {}자에서 {}자로 축소합니다.", text.length(), maxChars);
        return text.substring(0, maxChars) + "\n\n[문서가 너무 길어 일부만 요약되었습니다.]";
    }
}
