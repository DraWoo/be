package org.web.dragonwoo.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.web.dragonwoo.model.dto.MemberDto;
import org.web.dragonwoo.model.entity.Member;
import org.web.dragonwoo.repository.member.MemberRepository;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class MemberService {

    @Resource
    private MemberRepository memberRepository;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 비밀번호 암호화 저장
     * */
    public Member saveMember(MemberDto memberDto){
        Member member = Member.builder()
                .username(memberDto.getUsername())
                .password(bCryptPasswordEncoder.encode(memberDto.getPassword()))
                .phoneNum(memberDto.getPhoneNum())
                .enabled(true)
                .verificationCode(generateVerficationCode())
                .build();
        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username){
        return memberRepository.findByUsername(username);
    }

    public String generateVerficationCode(){
        Random random = new Random();
        int optCode = 100000 + random.nextInt(900000);
        return String.valueOf(optCode);
    }

    public Boolean verifyCode(String username, String inputCode){
        Optional<Member> optionalMember = memberRepository.findByUsername(username);
        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            return member.getVerificationCode().equals(inputCode);
        }
        return false;
    }

    public void updateVerificationCode(String username) {
        Optional<Member> optionalUser = memberRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            Member member = optionalUser.get();
            member.setVerificationCode(generateVerficationCode());
            memberRepository.save(member);
        }
    }
}
