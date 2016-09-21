package dev.bltucker.nanodegreecapstone.storydetail.data;



public class CommentDto {

    public final String by;

    public final long id;

    public final long[] kids;

    public final long parent;

    public final String text;

    public final long time;

    public CommentDto(String by, long id, long[] kids, long parent, String text, long time){
        this.by = by;
        this.id = id;
        this.kids = kids;

        this.parent = parent;
        this.text = text;
        this.time = time;
    }
}
