from wordcloud import WordCloud
import matplotlib.pyplot as plt


class WordCloudOut:
    word_list = []  # word list of cloud
    file_name = ""  # file name of cloud
    title = ""  # title of cloud

    def __init__(self, file_name, title, word_list):
        self.file_name = file_name
        self.title = title
        self.word_list = word_list
        self.createWordCloud(self.file_name, self.title, self.word_list)

    # create word cloud of list
    def createWordCloud(self, file_name, title, word_list):
        text = ""
        for x, y in word_list:
            text += x
            text += ' '

        wordcloud = WordCloud(width=800, height=400, relative_scaling=1.0).generate(text)
        plt.figure(figsize=(20, 10), facecolor='k')
        plt.imshow(wordcloud)
        fig = plt.gcf()  # get current figure
        plt.axis("off")
        # plt.show()
        plt.title(title)
        fig.savefig(file_name)  # save figure to pdf
