package dev.bltucker.nanodegreecapstone.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.data.CommentDto;
import retrofit2.http.Path;
import rx.Observable;

public class MockHackerNewsApiService implements HackerNewsApiService {

    private Random random;

    private List<Story> fakeStories;

    public MockHackerNewsApiService(Gson gson) {
        random = new Random(System.currentTimeMillis());
        fakeStories = new ArrayList<>();
        Story[] stories = gson.fromJson(FAKE_STORIES_JSON, Story[].class);
        fakeStories.addAll(Arrays.asList(stories));
    }


    @Override
    public Observable<List<Long>> getTopStoryIds() {
        List<Long> topStoryIds = Arrays.asList(TOP_STORY_IDS);
        return Observable.just(topStoryIds);
    }

    @Override
    public Observable<Story> getStory(@Path("storyId") long storyId) {
        return Observable.just(new Story(storyId, "Random test poster",
                getRandomScore(),
                System.currentTimeMillis(),
                getRandomTitle(),
                getRandomUrl(),
                getRandomCommentIds()));
    }

    @Override
    public Observable<CommentDto> getComment(@Path("commentId") long commentId) {
        //TODO generate some comments with children, but dont generate an endless tree of comments
        return Observable.just(new CommentDto("Author", 1L, new long[0], 0L, "Text", System.currentTimeMillis()));
    }

    public String getRandomAuthor() {
        return fakeStories.get(random.nextInt(fakeStories.size())).getPosterName();
    }

    private String getRandomTitle() {
        return fakeStories.get(random.nextInt(fakeStories.size())).getTitle();
    }

    private long getRandomScore() {
        return fakeStories.get(random.nextInt(fakeStories.size())).getScore();
    }

    public String getRandomUrl() {
        return fakeStories.get(random.nextInt(fakeStories.size())).getUrl();
    }

    private Long[] getRandomCommentIds() {
        return fakeStories.get(random.nextInt(fakeStories.size())).getCommentIds();
    }

    private static final Long[] TOP_STORY_IDS = new Long[]{
            12571200L,
            12571510L,
            12571383L,
            12571842L,
            12571595L,
            12572138L,
            12571707L,
            12552507L,
            12571094L,
            12571904L,
            12571026L,
            12551566L,
            12571620L,
            12570533L,
            12571095L,
            12571287L,
            12569238L,
            12571727L,
            12550834L,
            12571069L,
            12568070L,
            12569574L,
            12569503L,
            12569610L,
            12568863L,
            12555500L,
            12571046L,
            12562385L,
            12568246L,
            12561334L,
            12568998L,
            12571261L,
            12569182L,
            12562920L,
            12571033L,
            12563899L,
            12572028L,
            12561929L,
            12569930L,
            12570292L,
            12566503L,
            12564442L,
            12569858L,
            12571694L,
            12568829L,
            12569633L,
            12565023L,
            12571812L,
            12569417L,
            12570867L,
            12571791L,
            12568414L,
            12564298L,
            12563833L,
            12570490L,
            12569695L,
            12571715L,
            12564793L,
            12566056L,
            12564190L,
            12570395L,
            12564929L,
            12570770L,
            12567991L,
            12563798L,
            12559006L,
            12568250L,
            12568477L,
            12559169L,
            12567645L,
            12566326L,
            12562849L,
            12567578L,
            12567446L,
            12558053L,
            12566706L,
            12565460L,
            12558308L,
            12563398L,
            12558078L,
            12569813L,
            12559906L,
            12563661L,
            12563717L,
            12561302L,
            12566500L,
            12566343L,
            12563400L,
            12571281L,
            12559753L,
            12564410L,
            12568933L,
            12556160L,
            12567495L,
            12570932L,
            12558589L,
            12557943L,
            12570708L,
            12560902L,
            12557212L,
            12561979L};

    private static final String[] FAKE_COMMENT_TEXT = new String[]{"First!",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit",
            "at blandit nisi ultrices non. Nulla mollis augue et dolor porttitor, in ultrices orci consectetur. Aliquam tincidunt facilisis pellentesque.",
            "at tortor lacinia non",
            "modo. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nullam efficitur efficitur sem. Maecenas velit turpis, fringilla at eros et, rhoncus cursus neque. Nulla tempus posuere libero nec tempus. Sed aliquet, lacus ut ornare hendrerit, elit ",
            "My friend makes 8000 dollars an hour working from home!",
            "Aliquam sit amet dolor arcu",
            "tldr;",
            "Suspendisse congue massa eget ante cursus, et luctus urna commodo. Sed aliquet est quis bibendum fringilla. Quisque sit amet magna massa",
            "I didn't read the article but my opinion of it is....",
            "Nam ultricies, velit sit amet maximus convallis, dolor erat dictum ex, nec laoreet ex justo in est. Donec suscipit arcu quis nibh maximus ultricies"};

    private static final String FAKE_STORIES_JSON = "[\n" +
            "    {\n" +
            "        \"by\": \"natashabaker\",\n" +
            "        \"descendants\": 41,\n" +
            "        \"id\": 12571200,\n" +
            "        \"kids\": [\n" +
            "            12572122,\n" +
            "            12571262,\n" +
            "            12571890,\n" +
            "            12571263,\n" +
            "            12571896,\n" +
            "            12571660,\n" +
            "            12571689,\n" +
            "            12571800,\n" +
            "            12572068,\n" +
            "            12571668,\n" +
            "            12571483,\n" +
            "            12571571,\n" +
            "            12571596,\n" +
            "            12571600,\n" +
            "            12571564,\n" +
            "            12571311,\n" +
            "            12571667,\n" +
            "            12571438\n" +
            "        ],\n" +
            "        \"score\": 162,\n" +
            "        \"time\": 1474729591,\n" +
            "        \"title\": \"Show HN: InstaPart – Build circuit boards faster with instant parts\",\n" +
            "        \"type\": \"story\",\n" +
            "        \"url\": \"http://www.snapeda.com/instapart\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"by\": \"jonbaer\",\n" +
            "        \"descendants\": 20,\n" +
            "        \"id\": 12571510,\n" +
            "        \"kids\": [\n" +
            "            12571724,\n" +
            "            12571766,\n" +
            "            12571757,\n" +
            "            12571797,\n" +
            "            12572059,\n" +
            "            12571852,\n" +
            "            12571987\n" +
            "        ],\n" +
            "        \"score\": 44,\n" +
            "        \"time\": 1474734368,\n" +
            "        \"title\": \"Nature’s libraries are the fountains of biological innovation\",\n" +
            "        \"type\": \"story\",\n" +
            "        \"url\": \"https://aeon.co/essays/without-a-library-of-platonic-forms-evolution-couldn-t-work\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"by\": \"mmastrac\",\n" +
            "        \"descendants\": 6,\n" +
            "        \"id\": 12571383,\n" +
            "        \"kids\": [\n" +
            "            12571395,\n" +
            "            12571625\n" +
            "        ],\n" +
            "        \"score\": 86,\n" +
            "        \"time\": 1474732360,\n" +
            "        \"title\": \"Neural Photo Editor\",\n" +
            "        \"type\": \"story\",\n" +
            "        \"url\": \"https://github.com/ajbrock/Neural-Photo-Editor\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"by\": \"efavdb\",\n" +
            "        \"descendants\": 2,\n" +
            "        \"id\": 12571842,\n" +
            "        \"kids\": [\n" +
            "            12572040,\n" +
            "            12571999\n" +
            "        ],\n" +
            "        \"score\": 22,\n" +
            "        \"time\": 1474739601,\n" +
            "        \"title\": \"GPU Accelerated Theano and Keras with Windows 10\",\n" +
            "        \"type\": \"story\",\n" +
            "        \"url\": \"http://efavdb.com/gpu-accelerated-theano-keras-with-windows-10/\"\n" +
            "    },\n" +
            "    {\n" +
            "        \"by\": \"jackgavigan\",\n" +
            "        \"descendants\": 11,\n" +
            "        \"id\": 12571595,\n" +
            "        \"kids\": [\n" +
            "            12572083,\n" +
            "            12572111,\n" +
            "            12571900,\n" +
            "            12571880,\n" +
            "            12571859\n" +
            "        ],\n" +
            "        \"score\": 24,\n" +
            "        \"time\": 1474735741,\n" +
            "        \"title\": \"Bitcoin Wealth Distribution\",\n" +
            "        \"type\": \"story\",\n" +
            "        \"url\": \"https://blog.lawnmower.io/the-bitcoin-wealth-distribution-69a92cc4efcc\"\n" +
            "    }\n" +
            "]";

}
