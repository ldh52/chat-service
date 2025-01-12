package kr.co.yahopet.chatservice.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.co.yahopet.chatservice.dtos.ChatroomDto;
import kr.co.yahopet.chatservice.entities.Chatroom;
import kr.co.yahopet.chatservice.entities.Member;
import kr.co.yahopet.chatservice.entities.MemberChatroomMapping;
import kr.co.yahopet.chatservice.entities.Message;
import kr.co.yahopet.chatservice.repositories.ChatroomRepository;
import kr.co.yahopet.chatservice.repositories.MemberChatroomMappingRepository;
import kr.co.yahopet.chatservice.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final MemberChatroomMappingRepository memberChatroomMappingRepository;
    private final MessageRepository messageRepository;

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

        memberChatroomMappingRepository.save(memberChatroomMapping);

        return chatroom;
    }

    public Boolean joinChatroom(Member member, Long newChatroomId, Long currentChatroomId) {
        if (currentChatroomId != null) {
            updateLastCheckedAt(member, currentChatroomId);
        }

        if (memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),
            newChatroomId)) {
            log.info("이미 참여한 채팅방입니다.");
            return false;
        }

        Optional<Chatroom> optionalChatroom = chatroomRepository.findById(newChatroomId);
        if (optionalChatroom.isEmpty()) {
            log.warn("채팅방을 찾을 수 없습니다: {}", newChatroomId);
            return false;
        }

        Chatroom chatroom = optionalChatroom.get();

        MemberChatroomMapping memberChatroomMapping = MemberChatroomMapping.builder()
            .member(member)
            .chatroom(chatroom)
            .build();

        memberChatroomMappingRepository.save(memberChatroomMapping);

        return true;
    }

    private void updateLastCheckedAt(Member member, Long currentChatroomId) {
        Optional<MemberChatroomMapping> optionalMapping = memberChatroomMappingRepository
            .findByMemberIdAndChatroomId(member.getId(), currentChatroomId);

        if (optionalMapping.isPresent()) {
            MemberChatroomMapping memberChatroomMapping = optionalMapping.get();
            memberChatroomMapping.updateLastCheckedAt();
            memberChatroomMappingRepository.save(memberChatroomMapping);
        } else {
            log.warn("참여하지 않은 채팅방입니다: {}", currentChatroomId);
        }
    }

    @Transactional
    public Boolean leaveChatroom(Member member, Long chatroomId) {
        if (!memberChatroomMappingRepository.existsByMemberIdAndChatroomId(member.getId(),
            chatroomId)) {
            log.info("참여하지 않은 방입니다.");
            return false;
        }

        memberChatroomMappingRepository.deleteByMemberIdAndChatroomId(member.getId(), chatroomId);
        return true;
    }

    public List<Chatroom> getChatroomList(Member member) {
        List<MemberChatroomMapping> memberChatroomMappingList = memberChatroomMappingRepository.findByMemberId(
            member.getId());

        return memberChatroomMappingList.stream()
            .map(memberChatroomMapping -> {
                Chatroom chatroom = memberChatroomMapping.getChatroom();
                chatroom.setHasNewMessage(
                    messageRepository.existsByChatroomIdAndCreatedAtAfter(chatroom.getId(),
                        memberChatroomMapping.getLastCheckedAt()));
                return chatroom;
            })
            .toList();
    }

    public Message saveMessage(Member member, Long chatroomId, String text) {
        Optional<Chatroom> optionalChatroom = chatroomRepository.findById(chatroomId);
        if (optionalChatroom.isEmpty()) {
            log.warn("채팅방을 찾을 수 없습니다: {}", chatroomId);
            return null; // 또는 예외를 던질 수 있습니다.
        }

        Chatroom chatroom = optionalChatroom.get();

        Message message = Message.builder()
            .text(text)
            .member(member)
            .chatroom(chatroom)
            .createdAt(LocalDateTime.now())
            .build();

        return messageRepository.save(message);
    }

    public List<Message> getMessageList(Long chatroomId) {
        return messageRepository.findAllByChatroomId(chatroomId);
    }

    @Transactional(readOnly = true)
    public ChatroomDto getChatroom(Long chatroomId) {
        Optional<Chatroom> optionalChatroom = chatroomRepository.findById(chatroomId);
        if (optionalChatroom.isEmpty()) {
            log.warn("채팅방을 찾을 수 없습니다: {}", chatroomId);
            return null; // 또는 예외를 던질 수 있습니다.
        }

        Chatroom chatroom = optionalChatroom.get();
        return ChatroomDto.from(chatroom);
    }
}