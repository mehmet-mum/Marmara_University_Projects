import glob
import itertools
import operator
import math
import nltk
from nltk.corpus import stopwords
from DOCX import DOCX
from PDF import PDF
from TXT import TXT
from TextFile import TextFile
from TxtOut import TxtOut
from WordCloudOut import WordCloudOut
from nltk import word_tokenize


class Main(object):
    file_list = []  # list of all files
    stop_words = []  # list of all stop words
    documents = []  # all words in documents (each element is a different document)
    word_list = []  # tokenized words
    tf_list = []  # term frequency of words
    tf_list_50 = []  # most frequent 50 words(TF)
    idf_list = {}  # inverse document frequency of words
    tfidf_list = {}  # tf*idf values of words
    tfidf_list_50 = []  # most frequent 50 words(TF-IDF)
    txt_files = [TextFile for i in range(100)]  # all objects (txt,csv,pdf,docx)
    tf_list_file_name = "tf_list.csv"
    tf_idf_list_file_name = "tfidf_list.csv"
    tf_wordCloud_file_name = "tf_WordCloud.pdf"
    tf_wordCloud_title = "tf_WordCloud"
    tf_idf_wordCloud_file_name = "tfidf_wordCloud.pdf"
    tf_idf_wordCloud_title = "tf_idf_WordCloud"

    def __init__(self):
        self.createFileList()  # create file list
        self.createStopWords()  # specialize stop words
        self.readFiles(self.file_list)  # read files from list

        # term frequency operations
        self.calculateTermFrequency()  # calculates term frequency of all words
        self.tf_list_50 = list(itertools.islice(sorted(self.tf_list.items(), key=operator.itemgetter(1), reverse=True), 50))
        tf_list_obj = TxtOut(self.tf_list_file_name, self.tf_list_50)   # csv output for tf
        tf_wordCloud_obj = WordCloudOut(self.tf_wordCloud_file_name, self.tf_wordCloud_title, self.tf_list_50)  # word cloud for tf

        # inverse document frequency operations
        self.calculateIDF()    # calculate idf values

        # term frequency * inverse document frequency operations
        self.calculateTfIdf()   # calculate tf idf values
        self.tfidf_list_50 = list(itertools.islice(sorted(self.tfidf_list.items(), key=operator.itemgetter(1), reverse=True), 50))
        tfidf_list_obj = TxtOut(self.tf_idf_list_file_name, self.tfidf_list_50)  # csv output for tf-idf
        tfidf_wordCloud_obj = WordCloudOut(self.tf_idf_wordCloud_file_name, self.tf_idf_wordCloud_title, self.tfidf_list_50)    # word cloud for tf-idf

    # get all file names from defined path
    def createFileList(self):
        path = "/home/erhan/PycharmProjects/TextMining/"
        self.file_list = glob.glob(path + "*.pdf")
        self.file_list += glob.glob(path + "*.txt")
        self.file_list += glob.glob(path + "*.docx")

    # creates list of stop words
    def createStopWords(self):
        self.stop_words = stopwords.words('english')
        self.stop_words += "abstract", "introduction", "conclusions", "related", "work", "author", "university", "using"
        self.stop_words += "use", "used", "(", ")", "[", "]", "-", "_", "'", "/", "*", "{", "}", ",", ".", "#", "$"

    # read all files in file_list
    def readFiles(self, file_list):
        i = 0
        for file in file_list:
            print("Reading : " + file)
            # create pdf object
            if file.endswith("pdf"):
                self.txt_files[i] = PDF(file, self.stop_words)
            # create docx object4
            elif file.endswith("docx"):
                self.txt_files[i] = DOCX(file, self.stop_words)
            # create txt object
            elif file.endswith("txt") or file.endswith("csv"):
                self.txt_files[i] = TXT(file, self.stop_words)

            # append text of created object to documents list
            self.documents.append(self.txt_files[i].text)
            self.word_list += self.txt_files[i].word_list
            i += 1

    # calculates term frequency of all words
    def calculateTermFrequency(self):
        print("Calculating term frequency for all words.Please wait.")
        self.tf_list = nltk.Counter(self.word_list)

    # calculates inverse document frequency of all words
    def calculateIDF(self):
        print("Calculating inverse document frequency for all words.Please wait.")
        total_doc_num = len(self.tf_list)  # total documents count
        for word, value in self.tf_list.items():
            if int(value) == 1:
                doc_contains_word = 1
            else:
                doc_contains_word = self.docContainsWord(self.documents, str(word))
            if doc_contains_word == 0:
                print("'" + word + "'is not in word list")
                continue
            idf = math.log(total_doc_num / doc_contains_word, 10)
            self.idf_list[word] = [value, idf]

    # check how many documents contains the word
    def docContainsWord(self, documents, word):
        count = 0
        for i in range(len(documents)):
            for search in word_tokenize(documents[i]):
                if search == word:
                    count += 1
                    break
        return count

    # calculates tf-idf for all words
    def calculateTfIdf(self):
        for word, value in self.idf_list.items():
            self.tfidf_list[word] = float(value[0])*float(value[1])  # value[0] = tf value, value[1] = idf value

    # print tf*idf of all words
    def printTfIdf(self):
        print("Words with tf*idf")
        print("---------------------------")
        print(self.tfidf_list_50)

    # print idf of all words
    def printIDF(self):
        print("Words / Tf / IDF")
        print("---------------------------")
        print(self.idf_list)

    # prints tf of all words
    def printTF(self):
        print("Most frequent 50 words(TF)")
        print("---------------------------")
        print(self.tf_list_50)

    # prints tokenized words in all documents
    def printWordList(self):
        print("Words")
        print("---------------------------")
        print(self.word_list)

    # prints stop word list
    def printStopWords(self):
        print("Stop Words")
        print("---------------------------")
        print(self.stop_words)

    # prints file list
    def printFiles(self):
        print("Files")
        print("---------------------------")
        print(self.file_list)

    # prints text of all documents
    def printAllText(self):
        print("Documents")
        print("---------------------------")
        print(self.documents)


if __name__ == '__main__':
    m = Main()
    m.printFiles()
    m.printStopWords()
    m.printTF()
    m.printIDF()
    m.printTfIdf()
