from matplotlib import pyplot as plt
import os
from os import listdir


# file formatted as follows
# title,x axis label,y axis label,y1(dataset) label,y2(dataset) label
# x,y1,y2
# ...
def read(path):
    x = []
    y1 = []
    y2 = []
    with open(path, 'r') as f:
        for line in f:
            curr_ln = line.split(',')
            if len(curr_ln) == 5:
                title = curr_ln[0]
                x_lbl = curr_ln[1]
                y_lbl = curr_ln[2]
                y1_lbl = curr_ln[3]
                y2_lbl = curr_ln[4][:-1]  # removing \n
            else:
                x.append(curr_ln[0])
                y1.append(curr_ln[1])
                y2.append(curr_ln[2][:-1])  # removing \n

    return title, x_lbl, y_lbl, y1_lbl, y2_lbl, x, y1, y2


def plot(title, x_lbl, y_lbl, y1_lbl, y2_lbl, x, y1, y2):
    plt.clf()
    plt.scatter(x, y1, label=y1_lbl)
    if 'Speed up' not in title:
        plt.scatter(x, y2, label=y2_lbl)
    else:
        plt.plot([0] + x, [0] + x, label=y2_lbl)

    plt.xlabel(x_lbl)
    plt.ylabel(y_lbl)
    plt.title(title)
    plt.legend()

    plt.savefig(title + '.pdf')


def getFileNames():
    file_names = listdir(os.getcwd())
    return [filename for filename in file_names if filename.endswith('.csv')]


def main():
    for file in getFileNames():
        title, x_lbl, y_lbl, y1_lbl, y2_lbl, x, y1, y2 = read(file)
        plot(title, x_lbl, y_lbl, y1_lbl, y2_lbl, x, y1, y2)


if __name__ == '__main__':
    main()
