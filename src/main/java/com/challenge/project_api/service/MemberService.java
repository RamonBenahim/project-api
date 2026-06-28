package com.challenge.project_api.service;

import com.challenge.project_api.domain.entity.Member;
import com.challenge.project_api.dto.MemberDTO;
import com.challenge.project_api.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    
    public MemberDTO createMember(MemberDTO dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setRole(dto.getRole());
        member = memberRepository.save(member);
        dto.setId(member.getId());
        return dto;
    }
    
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(m -> {
                    MemberDTO dto = new MemberDTO();
                    dto.setId(m.getId());
                    dto.setName(m.getName());
                    dto.setRole(m.getRole());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Member getMemberEntity(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + id));
    }
}
