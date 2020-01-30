import csv


class TxtOut:
    file_name = ""  # file name of output file
    word_list = []  # words with frequency values

    def __init__(self, file_name, word_list):
        self.file_name = file_name
        self.word_list = word_list
        self.createCSV(self.file_name,self.word_list)

    # create file and write word list with frequency
    def createCSV(self, file_name, word_list):
        with open(file_name, 'w') as out:  # open file
            csv_out = csv.writer(out)
            for row in word_list:  # write to file row by row
                csv_out.writerow(row)
