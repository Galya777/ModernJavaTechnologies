package modernJava.week4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SocialNetworkImpl implements SocialNetwork {
    private final Set<UserProfile> users;
    private final Set<Post> posts;
    private final Map<UserProfile, Set<UserProfile>> friendsGraph;

    public SocialNetworkImpl() {
        this.users = new HashSet<>();
        this.posts = new HashSet<>();
        this.friendsGraph = new HashMap<>();
    }

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile cannot be null");
        }
        if (users.contains(userProfile)) {
            throw new UserRegistrationException("User already registered");
        }
        users.add(userProfile);
        friendsGraph.put(userProfile, new HashSet<>());
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if (userProfile == null || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("User profile and content cannot be null or empty");
        }
        if (!users.contains(userProfile)) {
            throw new UserRegistrationException("User not registered");
        }

        Post post = new SocialFeedPost(userProfile, content);
        posts.add(post);
        return post;
    }

    @Override
    public Collection<Post> getPosts() {
        return Collections.unmodifiableSet(posts);
    }

    @Override
    public Set<UserProfile> getReachedUsers(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Post cannot be null");
        }

        Set<UserProfile> result = new HashSet<>();
        UserProfile author = post.getAuthor();
        Set<Interest> authorInterests = (Set<Interest>) author.getInterests();

        for (UserProfile user : users) {
            if (user.equals(author)) {
                continue;
            }

            // Check if they share at least one interest
            boolean sharesInterest = false;
            for (Interest interest : user.getInterests()) {
                if (authorInterests.contains(interest)) {
                    sharesInterest = true;
                    break;
                }
            }

            if (sharesInterest && areConnected(user, author)) {
                result.add(user);
            }
        }

        return result;
    }

    @Override
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2)
            throws UserRegistrationException {
        if (userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("User profiles cannot be null");
        }
        if (!users.contains(userProfile1) || !users.contains(userProfile2)) {
            throw new UserRegistrationException("User not registered");
        }

        Set<UserProfile> mutualFriends = new HashSet<>(userProfile1.getFriends());
        mutualFriends.retainAll(userProfile2.getFriends());
        return mutualFriends;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> sortedUsers = new TreeSet<>(
                Comparator.comparingInt((UserProfile u) -> u.getFriends().size()).reversed()
                        .thenComparing(UserProfile::getUsername)
        );
        sortedUsers.addAll(users);
        return sortedUsers;
    }

    private boolean areConnected(UserProfile user1, UserProfile user2) {
        Set<UserProfile> visited = new HashSet<>();
        return dfs(user1, user2, visited);
    }

    private boolean dfs(UserProfile current, UserProfile target, Set<UserProfile> visited) {
        if (current.equals(target)) {
            return true;
        }

        visited.add(current);
        for (UserProfile friend : current.getFriends()) {
            if (!visited.contains(friend) && dfs(friend, target, visited)) {
                return true;
            }
        }

        return false;
    }
}

