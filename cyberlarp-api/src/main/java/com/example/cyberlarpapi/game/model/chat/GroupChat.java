package com.example.cyberlarpapi.game.model.chat;

import com.example.cyberlarpapi.User;
import com.example.cyberlarpapi.game.model.chat.message.ChatMessage;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "groupChat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GroupChatUser> users = new ArrayList<>();


    public void inviteUser(User user, Role role) {
        GroupChatUser groupChatUser = new GroupChatUser(user, this, role);
        users.add(groupChatUser);
    }

    public void acceptInvitation(User user) {
        for (GroupChatUser groupChatUser : users) {
            if (groupChatUser.getUser().equals(user)) {
                groupChatUser.setRole(Role.MEMBER);
            }
        }
    }

    public boolean hasAccess(User user) {
        for (GroupChatUser groupChatUser : users) {
            if (groupChatUser.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwner(User user) {
        for (GroupChatUser groupChatUser : users) {
            if (groupChatUser.getUser().equals(user) && groupChatUser.getRole() == Role.OWNER) {
                return true;
            }
        }
        return false;
    }

    public void removeUser(User user) {
        GroupChatUser userToRemove = null;
        for (GroupChatUser groupChatUser : users) {
            if (groupChatUser.getUser().equals(user)) {
                userToRemove = groupChatUser;
                break;
            }
        }
        if (userToRemove != null) {
            users.remove(userToRemove);
        }
    }
}