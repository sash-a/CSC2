import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * Ass1
 * Sasha
 * 2017/08/28.
 */
public class Timer
{
    private static final String SPEED_UP_FILE = "SpeedUp.csv";
    private static final String FILTER_SIZE_FILE = "FilterSize.csv";
    private static final String CUT_OFF_FILE = "CutOff.csv";
    private static final String DATA_SIZE_FILE = "DataSize.csv";
    private long startTime;

    public static void main(String[] args)
    {
        Timer t = new Timer();
        double[] totalData = t.read(1000000); // reading the whole file once

        t.writeAllHeaders();

        for (int i = 20000; i < 500000; i *= 2)
        {
            t.calcSpeedUp(7, i, totalData, 750000);
            t.calcCutOffImpact(7, i, totalData, 750000);
        }

        for (int i = 3; i <= 21; i += 2)
        {
            t.calcFilterSizeImpact(i, 50, totalData, 750000);
        }

        int[] dataSizeValues = {100, 500, 1000, 5000, 10000, 100000, 250000, 500000, 750000, 1000000};
        for (int dataSize : dataSizeValues)
        {
            t.calcDataSizeImpact(7, 50, totalData, dataSize);
        }
    }

    void writeAllHeaders()
    {
        writeHeader(SPEED_UP_FILE,
                "Speed up of parallel filter vs perfect linear speed up",
                "Number of processors",
                "Speed up",
                "Parallel filter speed up",
                "Perfect linear speed up");
        writeHeader(FILTER_SIZE_FILE,
                "Impact of the filter window size on the speed of parallel and sequential filtering",
                "Filter window size",
                "Time (s)",
                "Parallel filtering algorithm",
                "Sequential filtering algorithm");
        writeHeader(CUT_OFF_FILE,
                "Impact of the sequential cut off value on the speed of parallel and sequential filtering",
                "Sequential cut off value",
                "Time (s)",
                "Parallel filtering algorithm",
                "Sequential filtering algorithm");
        writeHeader(DATA_SIZE_FILE,
                "Impact of the data set size on the speed of parallel and sequential filtering",
                "Data set size",
                "Time (s)",
                "Parallel filtering algorithm",
                "Sequential filtering algorithm");
    }

    void calcSpeedUp(int filterSize, int sequentialCutoff, double[] totalData, int dataSize)
    {
        double avgParallelTime = timeIt(filterSize, sequentialCutoff, totalData, dataSize).get(0);

        double[] data = Arrays.copyOfRange(totalData, 0, dataSize);
        double speedUp = timeSequentialFilter(data, filterSize) / avgParallelTime;
        int numProcesses = getNumProcessors(dataSize, sequentialCutoff);

        write(SPEED_UP_FILE, numProcesses, speedUp, speedUp);
    }

    void calcFilterSizeImpact(int filterSize, int sequentialCutoff, double[] totalData, int dataSize)
    {
        List<Double> times = timeIt(filterSize, sequentialCutoff, totalData, dataSize);

        write(FILTER_SIZE_FILE, filterSize, times.get(0), times.get(1));
    }

    void calcCutOffImpact(int filterSize, int sequentialCutoff, double[] totalData, int dataSize)
    {
        List<Double> times = timeIt(filterSize, sequentialCutoff, totalData, dataSize);


        write(CUT_OFF_FILE, sequentialCutoff, times.get(0), times.get(1));
    }

    void calcDataSizeImpact(int filterSize, int sequentialCutoff, double[] totalData, int dataSize)
    {
        List<Double> times = timeIt(filterSize, sequentialCutoff, totalData, dataSize);

        write(DATA_SIZE_FILE, dataSize, times.get(0), times.get(1));
    }

    List<Double> timeIt(int filterSize, int sequentialCutoff, double[] totalData, int dataSize)
    {
        int repeats = 10;
        double[] data = Arrays.copyOfRange(totalData, 0, dataSize);

        ParallelFilter.sequentialCutoff = sequentialCutoff;

        double avgParallelTime = 0;
        double avgSequentialTime = 0;

        for (int i = 0; i < repeats + 1; i++)
        {
            if (i == 0) // warm up the timer
            {
                timeParallelFilter(data, filterSize);
                timeSequentialFilter(data, filterSize);
                continue;
            }

            float parallelTime = timeParallelFilter(data, filterSize);
            float sequentialTime = timeSequentialFilter(data, filterSize);

            if (parallelTime == -1f || sequentialTime == -1f)
            {
                System.out.println("Error: Parallel time " + parallelTime +
                        "\n Sequential time " + sequentialTime);
            }
            else
            {
                avgParallelTime += parallelTime;
                avgSequentialTime += sequentialTime;
            }
        }

        avgParallelTime /= 5;
        avgSequentialTime /= 5;

        return new ArrayList<Double>(Arrays.asList(avgParallelTime, avgSequentialTime));
    }

    int getNumProcessors(int dataSize, int sequentialCutoff)
    {
        int processors = 0;
        while (dataSize > sequentialCutoff)
        {
            dataSize /= 2;
            processors += 1;
        }
        return (int) Math.pow(2, processors);
    }

    float timeParallelFilter(double[] data, int filterSize)
    {
        ForkJoinPool fjp = new ForkJoinPool();

        tick();
        ParallelFilter pf = new ParallelFilter(data, filterSize);

        fjp.invoke(pf);
        if (pf.getFilteredList() != null)
        {
            return tock();
        }
        else
        {
            return -1f;
        }
    }

    float timeSequentialFilter(double[] data, int filterSize)
    {
        tick();
        SequentialFilter sf = new SequentialFilter(data, filterSize);
        double[] filtered = sf.filter();
        if (filtered != null)
        {
            return tock();
        }
        else
        {
            return -1f;
        }
    }

    double[] read(int numLines)
    {
        if (numLines > 1000000)
        {
            return null;
        }

        double data[] = new double[numLines];
        try
        {
            BufferedReader f = new BufferedReader(new FileReader("t.txt"));
            String s = f.readLine(); // read first irrelevant line
            s = f.readLine();
            for (int i = 0; i < numLines; i++)
            {
                data[i] = Double.parseDouble(s.split(" ")[1]);
                s = f.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    void write(String file, double x, double y1, double y2)
    {
        try
        {
            BufferedWriter f = new BufferedWriter(new FileWriter(file, true));
            f.write(x + "," + y1 + "," + y2);
            f.newLine();
            f.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    void writeHeader(String file,
                     String title,
                     String xAxisLabel,
                     String yAxisLabel,
                     String yOneLabel,
                     String yTwoLabel)
    {
        try
        {
            BufferedWriter f = new BufferedWriter(new FileWriter(file, true));
            f.write(title + "," + xAxisLabel + "," + yAxisLabel + "," + yOneLabel + "," + yTwoLabel);
            f.newLine();
            f.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void tick()
    {
        startTime = System.currentTimeMillis();
    }

    private float tock()
    {
        return (System.currentTimeMillis() - startTime) / 1000.0f;
    }
}
