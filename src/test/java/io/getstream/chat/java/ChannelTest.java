package io.getstream.chat.java;

import io.getstream.chat.java.models.Channel;
import io.getstream.chat.java.models.Channel.*;
import io.getstream.chat.java.models.DeleteStrategy;
import io.getstream.chat.java.models.Message.MessageRequestObject;
import io.getstream.chat.java.models.Sort;
import io.getstream.chat.java.models.Sort.Direction;
import io.getstream.chat.java.models.User;
import io.getstream.chat.java.models.User.ChannelMute;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChannelTest extends BasicTest {

  @DisplayName("Can retrieve channel by type")
  @Test
  void whenRetrievingChannel_thenNoException() {
    Assertions.assertDoesNotThrow(
        () ->
            Channel.getOrCreate(testChannel.getType(), null)
                .data(
                    ChannelRequestObject.builder()
                        .createdBy(testUserRequestObject)
                        .members(buildChannelMembersList())
                        .build())
                .request());
  }

  @DisplayName("Can add a moderator to a channel (update)")
  @Test
  void whenAddingModerator_thenHasModerator() {
    // We should not use testChannel to not modify it
    ChannelGetResponse channelGetResponse =
        Assertions.assertDoesNotThrow(() -> createRandomChannel());
    Assertions.assertEquals(0, countModerators(channelGetResponse.getMembers()));

    ChannelUpdateResponse channelUpdateResponse =
        Assertions.assertDoesNotThrow(
            () ->
                Channel.update(
                        channelGetResponse.getChannel().getType(),
                        channelGetResponse.getChannel().getId())
                    .user(testUserRequestObject)
                    .addModerators(Arrays.asList(testUserRequestObject.getId()))
                    .request());
    Assertions.assertEquals(1, countModerators(channelUpdateResponse.getMembers()));
  }

  private long countModerators(List<ChannelMember> members) {
    return members.stream()
        .filter(
            channelMember ->
                channelMember.getIsModerator() != null && channelMember.getIsModerator())
        .count();
  }

  @DisplayName("Can delete a channel")
  @Test
  void whenDeletingChannel_thenIsDeleted() {
    // We should not use testChannel to not delete it
    ChannelGetResponse channelGetResponse =
        Assertions.assertDoesNotThrow(() -> createRandomChannel());
    Assertions.assertNull(channelGetResponse.getChannel().getDeletedAt());
    Channel deletedChannel =
        Assertions.assertDoesNotThrow(
                () ->
                    Channel.delete(
                            channelGetResponse.getChannel().getType(),
                            channelGetResponse.getChannel().getId())
                        .request())
            .getChannel();
    Assertions.assertNotNull(deletedChannel.getDeletedAt());
  }

  @Test
  @DisplayName("Can delete many channels")
  void whenDeletingManyChannels_thenTaskIdIsReturned() {
    for (var deleteStrategy : List.of(DeleteStrategy.SOFT, DeleteStrategy.HARD)) {
      Assertions.assertDoesNotThrow(
          () -> {
            var ch1 = Assertions.assertDoesNotThrow(BasicTest::createRandomChannel).getChannel();
            Assertions.assertNotNull(ch1);
            var ch2 = Assertions.assertDoesNotThrow(BasicTest::createRandomChannel).getChannel();
            Assertions.assertNotNull(ch2);

            var cids = List.of(ch1.getCId(), ch2.getCId());
            var deleteManyResponse =
                Channel.deleteMany(cids).setDeleteStrategy(deleteStrategy).request();
            Assertions.assertNotNull(deleteManyResponse.getTaskId());
          });
    }
  }

  @DisplayName("Can list channels")
  @Test
  void whenListingChannels_thenNoException() {
    Assertions.assertDoesNotThrow(
        () ->
            Channel.list()
                .user(testUserRequestObject)
                .sort(Sort.builder().field("id").direction(Direction.DESC).build())
                .request());
  }

  @DisplayName("Can truncate channel")
  @Test
  void whenTruncateChannel_thenNoException() {
    // We should not use testChannel to not remove testMessage
    ChannelGetResponse channelGetResponse =
        Assertions.assertDoesNotThrow(() -> createRandomChannel());
    ChannelTruncateResponse resp =
        Assertions.assertDoesNotThrow(
            () ->
                Channel.truncate(
                        channelGetResponse.getChannel().getType(),
                        channelGetResponse.getChannel().getId())
                    .request());
    Assertions.assertNotNull(resp.getChannel());
  }

  @DisplayName("Can truncate channel with options")
  @Test
  void whenTruncateChannelWithOptions_thenNoException() {
    // We should not use testChannel to not remove testMessage
    ChannelGetResponse channelGetResponse =
        Assertions.assertDoesNotThrow(() -> createRandomChannel());
    MessageRequestObject message =
        MessageRequestObject.builder()
            .text("Truncate channel")
            .userId(testUserRequestObject.getId())
            .build();
    ChannelTruncateResponse resp =
        Assertions.assertDoesNotThrow(
            () ->
                Channel.truncate(
                        channelGetResponse.getChannel().getType(),
                        channelGetResponse.getChannel().getId())
                    .skipPush(true)
                    .message(message)
                    .request());
    Assertions.assertNotNull(resp.getChannel());
  }

  @DisplayName("Can query channel members")
  @Test
  void whenQueryingChannelMembers_thenRetrieveAll() {
    List<ChannelMember> channelMembers =
        Assertions.assertDoesNotThrow(
                () ->
                    Channel.queryMembers()
                        .id(testChannel.getId())
                        .type(testChannel.getType())
                        .request())
            .getMembers();
    Assertions.assertEquals(testUsersRequestObjects.size(), channelMembers.size());
  }

  @DisplayName("Can export channel")
  @Test
  void whenExportingChannel_thenNoException() {
    String taskId =
        Assertions.assertDoesNotThrow(
                () ->
                    Channel.export()
                        .channel(
                            ChannelExportRequestObject.builder()
                                .type(testChannel.getType())
                                .id(testChannel.getId())
                                .messagesUntil(new Date())
                                .build())
                        .request())
            .getTaskId();
    Assertions.assertNotNull(taskId);
  }

  @DisplayName("Can query the status of a channel export")
  @Test
  void whenQueryingExportChannelStatus_thenNoException() {
    String taskId =
        Assertions.assertDoesNotThrow(
                () ->
                    Channel.export()
                        .channel(
                            ChannelExportRequestObject.builder()
                                .type(testChannel.getType())
                                .id(testChannel.getId())
                                .build())
                        .request())
            .getTaskId();
    Assertions.assertDoesNotThrow(() -> Channel.exportStatus(taskId).request());
  }

  @DisplayName("Can hide a channel")
  @Test
  void whenHidingChannel_thenNoException() {
    // We should not use testChannel to not hide it
    ChannelGetResponse channelGetResponse =
        Assertions.assertDoesNotThrow(() -> createRandomChannel());
    Assertions.assertDoesNotThrow(
        () ->
            Channel.hide(
                    channelGetResponse.getChannel().getType(),
                    channelGetResponse.getChannel().getId())
                .user(testUserRequestObject)
                .request());
  }

  @DisplayName("Can mark all channel read")
  @Test
  void whenMarkingAllChannelsRead_thenNoException() {
    Assertions.assertDoesNotThrow(
        () -> Channel.markAllRead().user(testUserRequestObject).request());
  }

  @DisplayName("Can mark channel read")
  @Test
  void whenMarkingChannelsRead_thenNoException() {
    Assertions.assertDoesNotThrow(
        () ->
            Channel.markRead(testChannel.getType(), testChannel.getId())
                .user(testUserRequestObject)
                .request());
  }

  @DisplayName("Can mute a channel")
  @Test
  void whenMutingChannel_thenIsMuted() {
    // We should not use testChannel to not mute it
    Channel channel = Assertions.assertDoesNotThrow(() -> createRandomChannel()).getChannel();
    Assertions.assertFalse(isChannelMutedForTestUser(channel.getType(), channel.getId()));
    Assertions.assertDoesNotThrow(
        () ->
            Channel.mute()
                .channelCid(channel.getType() + ":" + channel.getId())
                .user(testUserRequestObject)
                .request());
    Assertions.assertTrue(isChannelMutedForTestUser(channel.getType(), channel.getId()));
  }

  private boolean isChannelMutedForTestUser(String channelType, String channelId) {
    List<ChannelMute> channelMutes =
        Assertions.assertDoesNotThrow(
            () ->
                User.list()
                    .filterCondition("id", testUserRequestObject.getId())
                    .request()
                    .getUsers()
                    .get(0)
                    .getChannelMutes());
    return channelMutes != null
        && channelMutes.stream()
            .filter(
                channelMute ->
                    channelMute.getChannel().getId().equals(channelId)
                        && channelMute.getChannel().getType().equals(channelType))
            .findAny()
            .isPresent();
  }

  @DisplayName("Can show a channel")
  @Test
  void whenShowingChannel_thenNoException() {
    Assertions.assertDoesNotThrow(
        () ->
            Channel.show(testChannel.getType(), testChannel.getId())
                .user(testUserRequestObject)
                .request());
  }

  @DisplayName("Can unmute a channel")
  @Test
  void whenUnMutingChannel_thenIsNotMutedAnymore() {
    // We should not use testChannel to not mute it
    Channel channel = Assertions.assertDoesNotThrow(() -> createRandomChannel()).getChannel();
    Assertions.assertFalse(isChannelMutedForTestUser(channel.getType(), channel.getId()));
    Assertions.assertFalse(isChannelMutedForTestUser(channel.getType(), channel.getId()));
    Assertions.assertDoesNotThrow(
        () ->
            Channel.mute()
                .channelCid(channel.getType() + ":" + channel.getId())
                .user(testUserRequestObject)
                .request());
    Assertions.assertTrue(isChannelMutedForTestUser(channel.getType(), channel.getId()));
    Assertions.assertDoesNotThrow(
        () ->
            Channel.unmute()
                .channelCid(channel.getType() + ":" + channel.getId())
                .user(testUserRequestObject)
                .request());
    Assertions.assertFalse(isChannelMutedForTestUser(channel.getType(), channel.getId()));
  }

  @DisplayName("Can update a channel (partial update)")
  @Test
  void whenUpdatingTeamWithPartialUpdate_thenIsUpdated() {
    // We should not use testChannel to not modify it
    Channel channel = Assertions.assertDoesNotThrow(() -> createRandomChannel()).getChannel();
    String updatedTeam = RandomStringUtils.randomAlphabetic(10);
    Channel updateChannel =
        Assertions.assertDoesNotThrow(
            () ->
                Channel.partialUpdate(channel.getType(), channel.getId())
                    .setValue("team", updatedTeam)
                    .user(testUserRequestObject)
                    .request()
                    .getChannel());
    Assertions.assertEquals(updatedTeam, updateChannel.getTeam());
  }
}
