package com.dockflow.backend.service.document.comment;

import com.dockflow.backend.dto.document.comment.DocumentCommentDTO;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentComment;
import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.repository.document.DocumentRepository;
import com.dockflow.backend.repository.document.comment.DocumentCommentRepository;
import com.dockflow.backend.repository.member.MemberRepository;
import com.dockflow.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentCommentService {

    private final DocumentCommentRepository documentCommentRepository;
    private final DocumentRepository documentRepository;
    private final MemberRepository memberRepository;

    // 댓글 작성
    @Transactional
    public DocumentCommentDTO createComment(Long documentNo, String content) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        DocumentComment comment = DocumentComment.builder()
                .document(document)
                .member(member)
                .content(content)
                .build();

        documentCommentRepository.save(comment);

        return DocumentCommentDTO.fromEntity(comment, memberId);
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<DocumentCommentDTO> getCommentsByDocumentNo(Long documentNo) {
        List<DocumentComment> comments = documentCommentRepository.findByDocument_DocumentNoOrderByCreatedAtAsc(documentNo);

        String currentMemberId = SecurityUtil.getCurrentMemberId();

        return comments.stream()
                .map(comment -> {
                    return DocumentCommentDTO.fromEntity(comment, currentMemberId);
                })
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public DocumentCommentDTO updateComment(Long commentNo, String content) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        DocumentComment comment = documentCommentRepository.findById(commentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        // 작성자 본인만 수정 가능
        if (!comment.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }

        comment.setContent(content);

        return DocumentCommentDTO.fromEntity(comment, memberId);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentNo) {
        String memberId = SecurityUtil.getCurrentMemberId();

        if (memberId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }

        DocumentComment comment = documentCommentRepository.findById(commentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if (!memberId.equals(comment.getMember().getMemberId())) {
            throw new IllegalArgumentException("댓글 작성자만 댓글을 삭제할 수 있습니다.");
        }

        documentCommentRepository.deleteById(commentNo);
    }
}
