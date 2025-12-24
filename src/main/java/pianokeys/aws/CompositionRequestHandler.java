package pianokeys.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;
import pianokeys.Playlist;
import pianokeys.net.CreateRequest;
import pianokeys.net.DeleteRequest;
import pianokeys.net.PlaylistResponse;
import pianokeys.net.UpdateRequest;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CompositionRequestHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse>
{

    private final Gson gson = new Gson();

    private final Playlist playlist = new Playlist(); // this is where the playlist will be saved to

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context)
    {
        try
        {
            String method = event.getRequestContext().getHttp().getMethod();
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

    private APIGatewayV2HTTPResponse handlePost(String body)
    {
        CreateRequest request = gson.fromJson(body, CreateRequest.class);
        playlist.add(request.composition());

        APIGatewayV2HTTPResponse apiResponse = new APIGatewayV2HTTPResponse();
        apiResponse.setStatusCode(201);
        return apiResponse;
    }

    private APIGatewayV2HTTPResponse handlePut(String body)
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

        APIGatewayV2HTTPResponse apiResponse = new APIGatewayV2HTTPResponse();
        apiResponse.setStatusCode(200);
        return apiResponse;
    }

    private APIGatewayV2HTTPResponse handleDelete(String body)
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

        APIGatewayV2HTTPResponse apiResponse = new APIGatewayV2HTTPResponse();
        apiResponse.setStatusCode(200);
        return apiResponse;
    }

    private APIGatewayV2HTTPResponse handleGet()
    {
        PlaylistResponse response = new PlaylistResponse(playlist);

        APIGatewayV2HTTPResponse apiResponse = new APIGatewayV2HTTPResponse();
        apiResponse.setStatusCode(200);
        apiResponse.setBody(gson.toJson(response));
        return apiResponse;
    }

    private APIGatewayV2HTTPResponse toResponseEvent(Exception e)
    {
        APIGatewayV2HTTPResponse apiResponse = new APIGatewayV2HTTPResponse();
        apiResponse.setStatusCode(500);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        apiResponse.setBody(stringWriter.toString());
        return apiResponse;
    }
}