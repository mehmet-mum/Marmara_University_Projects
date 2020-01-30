from TextFile import TextFile


class TXT(TextFile):

    def __init__(self, file_path, stop_words):
        super().__init__(file_path, stop_words)
        self.read(self.file_path)
        self.lowerCaseText()

    # get all text from txt or csv
    def read(self, file_path):
        try:
            f = open(file_path, 'r')  # open file
            for line in f.readlines():  # read line by line and append to text obj
                self.text += line
            self.removeChars(self.stop_words)  # remove integers, punctuation etc.
            f.close()  # close file
        except Exception as e:  # in case of error
            print(str(e))
