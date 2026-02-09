package com.dockflow.backend.controller.document;

import com.dockflow.backend.dto.document.DocumentCreateRequest;
import com.dockflow.backend.dto.document.DocumentResponse;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.service.document.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /* 문서 업로드 페이지 */
    @GetMapping("/upload")
    public String uploadPage(@RequestParam(value = "teamNo") Long teamNo, Model model) {
        model.addAttribute("teamNo", teamNo);
        model.addAttribute("categories", Document.DocumentCategory.values());
        return "/document/upload";
    }

    /* 문서 업로드 처리 */
    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam(value = "file") MultipartFile file,
            @Valid @ModelAttribute DocumentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("파일을 선택해주세요.");
            }

            documentService.uploadDocument(file, request, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", "문서가 업로드되었습니다.");

            return "redirect:/documents/list?teamNo=" + request.getTeamNo();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/documents/upload?teamNo=" + request.getTeamNo();
        }
    }

    /* 팀별 문서 목록 */
    @GetMapping("/list")
    public String documentList(
            @RequestParam(value = "teamNo") Long teamNo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentResponse> documentPage = documentService.getTeamDocuments(teamNo, userDetails.getUsername(), pageable);

        model.addAttribute("documents", documentPage.getContent());
        model.addAttribute("page", documentPage);
        model.addAttribute("teamNo", teamNo);

        return "document/list";
    }
}
