import java.util.concurrent.*;

/**
 * Ass1
 * Sasha
 * 2017/08/28.
 */
public class ParallelFilter extends RecursiveAction
{
    private double[] data;
    private int filterSize;
    private int hi;
    private int lo;
    public static int sequentialCutoff = 50;
    private double[] filteredList;

    public ParallelFilter(double[] data, int filterSize)
    {
        this.data = data;
        this.filterSize = filterSize;
        this.lo = 0;
        this.hi = data.length;
        this.filteredList = new double[this.hi];
    }

    private ParallelFilter(double[] data, int filterSize, int lo, int hi, double[] filteredList)
    {
        this.data = data;
        this.filterSize = filterSize;
        this.hi = hi;
        this.lo = lo;
        this.filteredList = filteredList;
    }

    /**
     * Check if the cutoff value will not get too small such that the array is larger than the filter size and as such
     * no filtering can be done
     *
     * @return true if the sequentialCutoff value is valid
     */
    private boolean isValidCutoff()
    {
        return sequentialCutoff >= 2 * filterSize;
    }


    /**
     * Checks if the filtering is possible with respect to the size of the filter and the data set
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
    public void filter(int lo, int hi)
    {
        int median = (filterSize - 1) / 2;

        for (int i = lo; i < hi; i++)
        {
            filteredList[i] = new Filter().filter(median, i, data);
        }
    }

    /**
     * Returns filteredList attribute.
     *
     * @return double[]
     */
    public double[] getFilteredList()
    {
        return this.filteredList;
    }

    /**
     * Runs the filter method in parallel
     */
    @Override
    protected void compute()
    {
        if (isValidCutoff() && isCorrectFilterSize())
        {
            if (hi - lo < sequentialCutoff)
            {
                filter(lo, hi);
            }
            else
            {
                ParallelFilter pfLeft = new ParallelFilter(
                        data,
                        filterSize,
                        lo,
                        (hi + lo) / 2,
                        filteredList);

                ParallelFilter pfRight = new ParallelFilter(
                        data,
                        filterSize,
                        (hi + lo) / 2,
                        hi,
                        filteredList);

                pfLeft.fork();
                pfRight.compute();
                pfLeft.join();
            }
        }
        else
        {
            this.filteredList = null;
        }
    }
}
