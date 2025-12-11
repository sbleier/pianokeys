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
            // methods: get, post, put, delete - then i figure out what request object i need to get and what i will do with it - the things that he said need to happen
            String method = event.getHttpMethod();
            // Retrieve the body and change json into an object
            String body = event.getBody();

            APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
            apiResponse.setStatusCode(200);

            if ("POST".equals(method))
            {
                CreateRequest request = gson.fromJson(body, CreateRequest.class);
                playlist.add(request.getComposition());
                apiResponse.setBody(gson.toJson(request.getComposition()));

            } else if ("PUT".equals(method))
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

                apiResponse.setBody(gson.toJson(request.getComposition()));

            } else if ("DELETE".equals(method))
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
                apiResponse.setBody("{\"message\": \"Deleted\"}");

            } else if ("GET".equals(method))
            {
                PlaylistResponse response = new PlaylistResponse(playlist);
                apiResponse.setBody(gson.toJson(response));
            }

            return apiResponse;
        } catch (Exception e)
        {
            // This prints the stack trace to the AWS log file
            e.printStackTrace();
            // This outputs the stack trace to the client
            return toResponseEvent(e);
        }
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