import string
from nltk import word_tokenize


class TextFile:
    file_count = 0  # number of files
    text = ""  # text of file
    file_path = ""  # path of file
    word_list = []  # tokenized text
    stop_words = []  # stop words

    def __init__(self, file_path, stop_words):
        self.file_path = file_path
        self.stop_words = stop_words
        TextFile.file_count += 1

    # returns number of files
    def getFileCount(self):
        return self.file_count

    # return all text
    def getText(self):
        return self.text

    # returns tokenized text
    def getWordList(self):
        return self.word_list

    # lower all text
    def lowerCaseText(self):
        self.text = self.text.lower()

    # removes integers, punctuation etc.
    def removeChars(self, stop_words):
        tokens = word_tokenize(self.text)
        tokens = [x for x in tokens if not (x.isdigit() or x[0] == '-' and x[1:].isdigit())]  # remove integers
        tokens = list(map(lambda x: x.lower(), tokens))  ##lowercase all strings

        self.word_list = [word for word in tokens if
                          not word in stop_words and not word in string.punctuation]  # add strings to list
