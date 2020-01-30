from docx2txt import docx2txt
from TextFile import TextFile


class DOCX(TextFile):

    def __init__(self, file_path, stop_words):
        super().__init__(file_path, stop_words)
        self.read(self.file_path)
        self.lowerCaseText()

    # get all text from docx
    def read(self, file_path):
        try:
            f = open(file_path, 'rb')  # open docx
            self.text = docx2txt.process(f)  # get text from docx
            self.removeChars(self.stop_words)  # remove integers, punctuation etc.
            f.close()  # close file
        except Exception as e:  # in case of error
            print(str(e))
