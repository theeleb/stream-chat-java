package io.stream.services;

import org.jetbrains.annotations.NotNull;
import io.stream.models.Event.EventSendRequestData;
import io.stream.models.Event.EventSendResponse;
import io.stream.models.Event.EventSendUserCustomRequestData;
import io.stream.models.framework.StreamResponseObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventService {

  @POST("channels/{type}/{id}/event")
  Call<EventSendResponse> send(
      @NotNull @Path("type") String channelType,
      @NotNull @Path("id") String channelId,
      @NotNull @Body EventSendRequestData eventSendRequestData);

  @POST("users/{user_id}/event")
  Call<StreamResponseObject> sendUserCustom(
      @NotNull @Path("user_id") String userId,
      @NotNull @Body EventSendUserCustomRequestData eventSendUserCustomRequestData);
}
