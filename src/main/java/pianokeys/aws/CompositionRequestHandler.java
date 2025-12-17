package pianokeys.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import pianokeys.CreateRequest;
import pianokeys.DeleteRequest;
import pianokeys.PlaylistResponse;
import pianokeys.UpdateRequest;
import pianokeys.Playlist;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CompositionRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent,
        APIGatewayProxyResponseEvent>
{

    private final Gson gson = new Gson();

    private final Playlist playlist = new Playlist(); // this is where the playlist will be saved to

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context)
    {
        try
        {
            String method = event.getHttpMethod();
            String body = event.getBody();

            return switch (method)
            {
                case "POST" -> handlePost(body);
                case "PUT" -> handlePut(body);
                case "DELETE" -> handleDelete(body);
                case "GET" -> handleGet();
                default -> throw new RuntimeException(method + " was not handled");
            };
        } catch (Exception e)
        {
            // This prints the stack trace to the AWS log file
            e.printStackTrace();
            // This outputs the stack trace to the client
            return toResponseEvent(e);
        }
    }

    private APIGatewayProxyResponseEvent handlePost(String body)
    {
        CreateRequest request = gson.fromJson(body, CreateRequest.class);
        playlist.add(request.getComposition());

        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        apiResponse.setStatusCode(201);
        return apiResponse;
    }

    private APIGatewayProxyResponseEvent handlePut(String body)
    {
        UpdateRequest request = gson.fromJson(body, UpdateRequest.class);

        for (int i = 0; i < playlist.size(); i++)
        {
            if (playlist.get(i).getId() == request.getComposition().getId())
            {
                playlist.set(i, request.getComposition());
                break;
            }
        }

        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        apiResponse.setStatusCode(200);
        return apiResponse;
    }

    private APIGatewayProxyResponseEvent handleDelete(String body)
    {
        DeleteRequest request = gson.fromJson(body, DeleteRequest.class);

        for (int i = 0; i < playlist.size(); i++)
        {
            if (playlist.get(i).getId() == request.getId())
            {
                playlist.remove(i);
                break;
            }
        }

        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        apiResponse.setStatusCode(200);
        return apiResponse;
    }

    private APIGatewayProxyResponseEvent handleGet()
    {
        PlaylistResponse response = new PlaylistResponse(playlist);

        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        apiResponse.setStatusCode(200);
        apiResponse.setBody(gson.toJson(response));
        return apiResponse;
    }

    private APIGatewayProxyResponseEvent toResponseEvent(Exception e)
    {
        APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
        apiResponse.setStatusCode(500);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        apiResponse.setBody(stringWriter.toString());
        return apiResponse;
    }
}