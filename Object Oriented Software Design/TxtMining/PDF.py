import PyPDF2
from TextFile import TextFile


class PDF(TextFile):

    def __init__(self, file_path, stop_words):
        super().__init__(file_path, stop_words)
        self.read(self.file_path)
        self.lowerCaseText()

    # get all text from pdf
    def read(self, file_path):
        try:
            f = open(file_path, 'rb')  # open pdf
            pdfReader = PyPDF2.PdfFileReader(f)  # create pdf reader
            num_pages = pdfReader.numPages  # number of pages that pdf has
            count = 0
            while count < num_pages:  # get all text from pdf file
                pageObj = pdfReader.getPage(count)
                count += 1
                self.text += pageObj.extractText()

            self.removeChars(self.stop_words)  # remove integers, punctuation etc.

            f.close()  # close file
        except Exception as e:  # in case of error
            print(str(e))
