package dev.bltucker.nanodegreecapstone.storydetail;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import dev.bltucker.nanodegreecapstone.models.Comment;

public class CommentTypeAdapter implements JsonDeserializer<Comment> {

    @Override
    public Comment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = ((JsonObject) json);

        long id = jsonObject.has("id") ? jsonObject.get("id").getAsLong() : 0;
        String authorName = jsonObject.has("by") ? jsonObject.get("by").getAsString() : "";
        String commentText = jsonObject.has("text") ? jsonObject.get("text").getAsString() : "";
        long time = jsonObject.has("time") ? jsonObject.get("time").getAsLong() : 0;

        long[] replyIds = new long[0];
        if(jsonObject.has("kids") && !jsonObject.get("kids").isJsonNull() && jsonObject.get("kids").isJsonArray()){
            JsonArray kids = jsonObject.get("kids").getAsJsonArray();
            replyIds = new long[kids.size()];

            for (int i = 0; i < kids.size(); i++) {
                replyIds[i] = kids.get(i).getAsLong();
            }
        }

        return new Comment(id, authorName, commentText, time, replyIds);

    }
}
