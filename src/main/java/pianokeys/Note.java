package pianokeys;

public class Note
{
    private String key;
    private double startTime;
    private double endTime;

    public Note(String key, double startTime, double endTime)
    {
        this.key = key;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getKey()
    {
        return key;
    }

    public double getStartTime()
    {
        return startTime;
    }

    public double getEndTime()
    {
        return endTime;
    }

}
