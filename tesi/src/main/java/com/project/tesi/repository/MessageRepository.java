package com.project.tesi.repository;

import com.project.tesi.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.timeStamp DESC")
    List<Message> findMessagesByChatId(@Param("chatId") Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.timeStamp DESC LIMIT 1")
    Message findLastMessageByChatId(@Param("chatId") Long chatId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.user.id != :userId AND m.isRead = false")
    int countUnreadMessagesByChatIdAndUserId(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.user.id != :userId AND m.isRead = false AND m.chat.id IN (SELECT c.id FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId)")
    int countTotalUnreadMessagesByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.user.id != :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId);
}

