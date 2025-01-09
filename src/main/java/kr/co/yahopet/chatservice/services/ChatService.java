package kr.co.yahopet.chatservice.services;

import java.time.LocalDateTime;
import java.util.List;
import kr.co.yahopet.chatservice.entity.Chatroom;
import kr.co.yahopet.chatservice.entity.Member;
import kr.co.yahopet.chatservice.entity.MemberChatroomMapping;
import kr.co.yahopet.chatservice.repositories.ChatroomRepository;
import kr.co.yahopet.chatservice.repositories.MemberChatroomMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MemberChatroomMappingRepository memberChatroomMappingRepository;

    public Chatroom createChatroom(Member member, String title) {
        Chatroom chatroom = Chatroom.builder()
            .title(title)
            .createdAt(LocalDateTime.now())
            .build();

        chatroom = chatroomRepository.save(chatroom);

        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
            .member(member)
            .chatroom(chatroom)
            .build();

        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return chatroom;
    }

    public boolean joinChatroom(Member member, Long chatroomId) {
        if (memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),
            chatroomId)) {
            log.info("이미 참여한 채팅방입니다.");
            return false;
        }

        Chatroom chatroom = chatroomRepository.findById(chatroomId).get();

        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
            .member(member)
            .chatroom(chatroom)
            .build();

        memberChatroomMapping = memberChatroomMappingRepository.save(memberChatroomMapping);

        return true;
    }

    public boolean leaveChatroom(Member member, Long chatroomId) {
        if (!memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),
            chatroomId)) {
            log.info("참여하지 않은 방입니다.");
            return false;
        }

        memberChatroomMappingRepository.deleteByMemberIdAndChatroomId(member.getId(), chatroomId);

        return true;
    }

    public List<Chatroom> chatroomList(Member member) {
        List<MemberChatroomMapping> memberChatroomMappingList = memberChatroomMappingRepository.findByMemberId(
            member.getId());

        return memberChatroomMappingList.stream()
            .map(MemberChatroomMapping::getChatroom)
            .toList();
    }

}
