/**
 * Ass1
 * Sasha
 * 2017/08/28.
 */
public class SequentialFilter extends Filter
{
    private double[] data;
    private int filterSize;

    public SequentialFilter(double[] data, int filterSize)
    {
        this.data = data;
        this.filterSize = filterSize;
    }

    /**
     * Checks if the filtering is possible
     *
     * @return true if the filtering is possible
     */
    private boolean isCorrectFilterSize()
    {
        int median = (filterSize - 1) / 2;
        return filterSize % 2 != 0 && filterSize <= data.length - 2 * median;
    }

    /**
     * Filters a list of numbers using a sequential algorithm
     *
     * @return A filtered version of the data attribute
     */
    public double[] filter()
    {
        if (!isCorrectFilterSize())
            return null;

        double filteredData[] = new double[data.length];
        int median = (filterSize - 1) / 2;
        for (int i = 0; i < data.length; i++)
        {
            filteredData[i] = new Filter().filter(median, i, data);
        }
        return filteredData;
    }
}
