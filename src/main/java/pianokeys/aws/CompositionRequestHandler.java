package pianokeys.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
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
            // CompositionRequest request = gson.fromJson(body, CompositionRequest.class);

            // Do something with the request and create a CompositionResponse
            // CompositionResponse response = new CompositionResponse("Received composition request");

            // Create the HTTP response with the CompositionResponse
            // String responseJson = gson.toJson(response);
            APIGatewayProxyResponseEvent apiResponse = new APIGatewayProxyResponseEvent();
            apiResponse.setStatusCode(200);
            // apiResponse.setBody(responseJson);
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