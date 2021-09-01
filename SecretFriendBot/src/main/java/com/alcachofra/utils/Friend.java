package com.alcachofra.utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Friend implements Comparable<Friend> {
    String name;
    List<Friend> secretFriends = new ArrayList<>();
    int used = 0;

    public Friend(@NotNull String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addSecretFriend(Friend friend) {
        secretFriends.add(friend);
    }

    public void use() {
        used++;
    }

    public int getUsed() {
        return used;
    }

    public List<Friend> getSecretFriends() {
        return secretFriends;
    }

    @Override
    public int compareTo(@NotNull Friend f) {
        return getName().compareTo(f.getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getName().equals(((Friend) o).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    public static void randomiseSecretFriends(List<Friend> friends, int num) {
        List<Friend> aux = new ArrayList<>(friends);
        Collections.shuffle(friends);

        for (Friend friend : friends) {
            Collections.shuffle(aux);
            for (Friend toAdd : aux) {
                if (!toAdd.equals(friend) && toAdd.getUsed() < num) {
                    if (friend.getSecretFriends().size() < num) {
                        friend.addSecretFriend(toAdd);
                        toAdd.use();
                    }
                    else break;
                }
            }
        }
    }

    public static boolean validateSecretFriends(List<Friend> friends, int num) {
        for (Friend friend : friends) {
            if (friend.getSecretFriends().size() < num) {
                return false;
            }
        }
        return true;
    }
}
