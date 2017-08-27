package dev.bltucker.nanodegreecapstone.storydetail.data;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import dev.bltucker.nanodegreecapstone.CapstoneApplication;
import dev.bltucker.nanodegreecapstone.data.MockHackerNewsApiService;
import dev.bltucker.nanodegreecapstone.events.EventBus;
import dev.bltucker.nanodegreecapstone.injection.ApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerApplicationComponent;
import dev.bltucker.nanodegreecapstone.injection.DaggerInjector;
import dev.bltucker.nanodegreecapstone.injection.TestApplicationResourcesModule;
import dev.bltucker.nanodegreecapstone.models.Story;
import dev.bltucker.nanodegreecapstone.storydetail.DetailStory;
import dev.bltucker.nanodegreecapstone.storydetail.events.StoryCommentsDownloadCompleteEvent;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class InterruptibleDownloadServiceTest {

    CountDownLatch testLatch;

    @Before
    public void setup() throws TimeoutException {
        Context context = getInstrumentation().getTargetContext().getApplicationContext();

        final ApplicationComponent applicationComponent = DaggerApplicationComponent.builder()
            .applicationResourcesModule(new TestApplicationResourcesModule((CapstoneApplication) context))
            .build();

        DaggerInjector.initializeInjector(applicationComponent);

        EventBus eventBus = DaggerInjector.getApplicationComponent().eventBus();

        testLatch = new CountDownLatch(1);
        eventBus.subscribeTo(StoryCommentsDownloadCompleteEvent.class)
                .subscribeWith(new Observer<Object>() {
                    Disposable disposable;
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Object o) {
                        testLatch.countDown();
                        if(disposable != null && !disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }
                });
    }

    @Test
    public void testServiceDownloadsCommentsAsExpected() throws Exception {
        Long[] commentIds = new Long[]{23L, 24L, 33L};
        int expectedCommentCount = commentIds.length;
        long fakeStoryId = 151L;
        setupFakeComments(fakeStoryId, commentIds);
        Story fakeStory = setupFakeStory(commentIds, fakeStoryId);

        DetailStory fakeDetailStory = new DetailStory(fakeStory);


        Context context = getInstrumentation().getTargetContext().getApplicationContext();
//
        Intent intent = new Intent(context, InterruptibleDownloadService.class);
        intent.putExtra(InterruptibleDownloadService.STORY_PARAM, fakeDetailStory);
        InterruptibleDownloadService.startDownload(context, fakeDetailStory);


        testLatch.await(15, TimeUnit.SECONDS);
//
//        ContentResolver contentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();
//        Cursor commentCursor = contentResolver.query(SchematicContentProviderGenerator.CommentPaths.withParentId(String.valueOf(fakeStoryId)),
//                null,
//                null,
//                null,
//                null);
//
//        assertEquals(expectedCommentCount, commentCursor.getCount());
//        commentCursor.close();
    }

    @NonNull
    private Story setupFakeStory(Long[] commentIds, long fakeStoryId) {
        return new Story(fakeStoryId,
            "Service Test Poster",
            200,
            System.currentTimeMillis(),
            "Service Test STory Title",
            "https://google.com/",
            commentIds);
    }

    private void setupFakeComments(long fakeStoryId, Long[] commentIds) {
        MockHackerNewsApiService fakeService = ((MockHackerNewsApiService) DaggerInjector.getApplicationComponent().hackerNewsApiService());

        Map<Long, CommentDto> fakeCommentMap = new HashMap<>();
        for (int i = 0; i < commentIds.length; i++) {
            fakeCommentMap.put(commentIds[i], new CommentDto("Service Test Author",
                commentIds[i],
                new long[0],
                fakeStoryId,
                "Fake Comment Test: " + commentIds[i],
                System.currentTimeMillis()));
        }

        fakeService.setFakeCommentDtos(fakeCommentMap);
    }


}