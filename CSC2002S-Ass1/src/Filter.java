import java.util.Arrays;

/**
 * Ass1
 * Sasha
 * 2017/08/28.
 */
public class Filter
{
    public double filter(int median, int position, double[] data)
    {
        if (position < median || position > data.length - median - 1)
        {
            return data[position];
        }
        else
        {
            double[] subList = Arrays.copyOfRange(data, position - median, position + median + 1);
            Arrays.sort(subList);
            return subList[median];
        }
    }
}