package dev.bltucker.nanodegreecapstone.data;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dev.bltucker.nanodegreecapstone.models.Comment;
import dev.bltucker.nanodegreecapstone.models.Story;
import retrofit2.http.Path;
import rx.Observable;

public class MockHackerNewsApiService implements HackerNewsApiService {

    private Random random;

    private List<Story> fakeStories;

    public MockHackerNewsApiService(Gson gson) {
        random = new Random(System.currentTimeMillis());
        fakeStories = new ArrayList<>();
        List list = gson.fromJson(FAKE_STORIES_JSON, fakeStories.getClass());
        fakeStories.addAll(list);
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
    public Observable<Comment> getComment(@Path("commentId") long commentId) {
        //TODO generate some comments with children, but dont generate an endless tree of comments
        return Observable.just(new Comment(commentId,
                getRandomAuthor(),
                "",
                System.currentTimeMillis(),
                new long[0]));
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
            12561979L,
            12566564L,
            12564233L,
            12564173L,
            12563702L,
            12563914L,
            12564038L,
            12556126L,
            12552094L,
            12553417L,
            12565773L,
            12557782L,
            12568836L,
            12555858L,
            12563852L,
            12569642L,
            12557304L,
            12558606L,
            12561495L,
            12561369L,
            12564385L,
            12556222L,
            12556240L,
            12567327L,
            12557808L,
            12568914L,
            12561928L,
            12567383L,
            12561209L,
            12558291L,
            12564064L,
            12568759L,
            12568757L,
            12551760L,
            12565449L,
            12557335L,
            12569206L,
            12555906L,
            12553591L,
            12556262L,
            12556986L,
            12570188L,
            12551863L,
            12559110L,
            12570642L,
            12557011L,
            12566799L,
            12559189L,
            12566772L,
            12567929L,
            12567848L,
            12560264L,
            12551814L,
            12555810L,
            12570222L,
            12565630L,
            12570035L,
            12554334L,
            12566381L,
            12560400L,
            12562019L,
            12562631L,
            12566958L,
            12555752L,
            12565084L,
            12552197L,
            12561966L,
            12566885L,
            12553740L,
            12557777L,
            12555773L,
            12563803L,
            12559936L,
            12554884L,
            12567689L,
            12568236L,
            12561079L,
            12562392L,
            12558104L,
            12552782L,
            12560709L,
            12563904L,
            12552003L,
            12556307L,
            12551623L,
            12549458L,
            12556848L,
            12557662L,
            12567364L,
            12560625L,
            12560488L,
            12569281L,
            12569621L,
            12560542L,
            12555984L,
            12566875L,
            12548414L,
            12552298L,
            12552563L,
            12556433L,
            12556645L,
            12566574L,
            12566532L,
            12560284L,
            12566374L,
            12558538L,
            12556609L,
            12562856L,
            12563692L,
            12552745L,
            12559270L,
            12558718L,
            12556140L,
            12549418L,
            12569327L,
            12568123L,
            12569906L,
            12552217L,
            12566169L,
            12554807L,
            12552157L,
            12553376L,
            12552357L,
            12567169L,
            12559624L,
            12559515L,
            12569791L,
            12561018L,
            12568552L,
            12552288L,
            12559100L,
            12549874L,
            12548608L,
            12556670L,
            12554182L,
            12553936L,
            12550528L,
            12552103L,
            12555160L,
            12568781L,
            12553444L,
            12553445L,
            12566475L,
            12552176L,
            12560760L,
            12568600L,
            12554432L,
            12554024L,
            12560234L,
            12569492L,
            12558814L,
            12568672L,
            12568989L,
            12560605L,
            12557372L,
            12568392L,
            12557134L,
            12563827L,
            12568305L,
            12561610L,
            12559217L,
            12559173L,
            12566560L,
            12556979L,
            12568842L,
            12568831L,
            12558587L,
            12558329L,
            12553690L,
            12551207L,
            12558502L,
            12568669L,
            12567766L,
            12564455L,
            12560327L,
            12566580L,
            12569216L,
            12552623L,
            12551175L,
            12569153L,
            12562163L,
            12559027L,
            12553373L,
            12558362L,
            12558342L,
            12555718L,
            12557354L,
            12567827L,
            12560714L,
            12557791L,
            12553688L,
            12563700L,
            12548792L,
            12567515L,
            12559558L,
            12567160L,
            12566061L,
            12568629L,
            12560637L,
            12560626L,
            12563934L,
            12552056L,
            12563792L,
            12568483L,
            12557543L,
            12548488L,
            12568380L,
            12559585L,
            12566280L,
            12566175L,
            12567957L,
            12567842L,
            12559201L,
            12557686L,
            12551178L,
            12561591L,
            12551018L,
            12556944L,
            12562147L,
            12557052L,
            12553981L,
            12567313L,
            12551660L,
            12550284L,
            12567260L,
            12567254L,
            12567183L,
            12563387L,
            12553133L,
            12563095L,
            12553044L,
            12566674L,
            12559183L,
            12556572L,
            12552093L,
            12555403L,
            12566181L,
            12551892L,
            12562343L,
            12556680L,
            12551218L,
            12560859L,
            12558877L,
            12559197L,
            12561687L,
            12550532L,
            12550389L,
            12550281L,
            12555825L,
            12562492L,
            12553858L,
            12554827L,
            12562033L,
            12553048L,
            12552820L,
            12552564L,
            12551810L,
            12551680L,
            12561030L,
            12567510L,
            12560609L,
            12555076L,
            12549615L,
            12550484L,
            12553045L,
            12548871L,
            12557020L,
            12561748L,
            12552644L,
            12563336L,
            12560599L,
            12562952L,
            12558710L,
            12559828L,
            12559681L,
            12554728L,
            12551637L,
            12558509L,
            12550736L,
            12562258L,
            12559901L,
            12550051L,
            12562204L,
            12557403L,
            12559247L,
            12554494L,
            12556074L,
            12557590L,
            12553185L,
            12551331L,
            12549829L,
            12561649L,
            12557649L,
            12551831L,
            12557501L,
            12561427L,
            12557286L,
            12550901L,
            12561148L,
            12557149L,
            12550020L,
            12560797L,
            12554291L,
            12552346L,
            12560468L,
            12556154L,
            12560077L,
            12551646L,
            12559686L,
            12555656L,
            12559492L,
            12555609L,
            12553423L,
            12553367L,
            12558859L,
            12558742L,
            12558064L,
            12554802L,
            12562787L,
            12557158L,
            12556842L,
            12556380L,
            12555945L,
            12550086L,
            12556498L,
            12552694L,
            12552330L,
            12551279L,
            12558682L,
            12554764L,
            12549426L,
            12553842L,
            12553704L,
            12553604L,
            12570862L,
            12553111L,
            12552748L,
            12552420L,
            12551231L,
            12551925L,
            12548610L,
            12550873L,
            12550137L,
            12571521L,
            12548656L,
            12549101L,
            12558585L,
            12556396L,
            12565905L,
            12563718L,
            12571101L,
            12553040L,
            12564561L,
            12569374L,
            12556470L,
            12559215L,
            12566869L,
            12569055L,
            12553261L,
            12563685L,
            12554315L,
            12550856L,
            12565380L,
            12566071L,
            12552221L,
            12564708L,
            12548808L,
            12559281L,
            12565514L};

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
