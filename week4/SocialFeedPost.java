package modernJava.week4;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class SocialFeedPost implements Post {
    private final String id;
    private final UserProfile author;
    private final LocalDateTime publishedOn;
    private final String content;
    private final Map<UserProfile, ReactionType> reactions;
    private final Map<ReactionType, Set<UserProfile>> reactionsByType;

    public SocialFeedPost(UserProfile author, String content) {
        if (author == null || content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Author and content cannot be null or empty");
        }

        this.id = UUID.randomUUID().toString();
        this.author = author;
        this.publishedOn = LocalDateTime.now();
        this.content = content;
        this.reactions = new HashMap<>();
        this.reactionsByType = new EnumMap<>(ReactionType.class);
    }

    @Override
    public String getUniqueId() {
        return id;
    }

    @Override
    public UserProfile getAuthor() {
        return author;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if (userProfile == null || reactionType == null) {
            throw new IllegalArgumentException("User profile and reaction type cannot be null");
        }

        boolean isUpdate = reactions.containsKey(userProfile);
        ReactionType previousReaction = reactions.put(userProfile, reactionType);

        if (isUpdate) {
            reactionsByType.get(previousReaction).remove(userProfile);
        }

        reactionsByType.computeIfAbsent(reactionType, k -> new HashSet<>()).add(userProfile);
        return !isUpdate;
    }

    @Override
    public boolean removeReaction(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile cannot be null");
        }

        ReactionType reaction = reactions.remove(userProfile);
        if (reaction != null) {
            reactionsByType.get(reaction).remove(userProfile);
            return true;
        }
        return false;
    }

    @Override
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        Map<ReactionType, Set<UserProfile>> result = new EnumMap<>(ReactionType.class);
        reactionsByType.forEach((k, v) -> result.put(k, Collections.unmodifiableSet(v)));
        return Collections.unmodifiableMap(result);
    }

    @Override
    public int getReactionCount(ReactionType reactionType) {
        if (reactionType == null) {
            throw new IllegalArgumentException("Reaction type cannot be null");
        }
        return reactionsByType.getOrDefault(reactionType, Collections.emptySet()).size();
    }

    @Override
    public int totalReactionsCount() {
        return reactions.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialFeedPost that = (SocialFeedPost) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
