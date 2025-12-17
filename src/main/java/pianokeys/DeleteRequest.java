package pianokeys;

public class DeleteRequest
{
    int id;

    // Constructor
    public DeleteRequest(int id) {
        this.id = id;
    }

    // Getter
    public int getId() {
        return id;
    }
}