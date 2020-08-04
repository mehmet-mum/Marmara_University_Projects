from flask import Flask, request, render_template
from socket import *

app = Flask(__name__, template_folder="templates")

HOST = '127.0.0.1'  # The server's hostname or IP address
PORT = 65430     # The port used by the server

# TCP connection with agency
def run(amount, ddate, adate, airline, hotel):
    with socket(AF_INET, SOCK_STREAM) as s:
        try:
            s.connect((HOST, PORT))
        except ConnectionRefusedError:
            print('Connection is refused')
            return 1
        try:
            string = amount + '&' +  ddate + '&' + adate + '&' + airline + '&' + hotel
            s.send(string.encode())
            data = s.recv(1024).decode()
            return data
        except KeyboardInterrupt:
            pass
        finally:
            s.close()

# load html 
@app.route("/")
def home():
    return render_template("index.html", confirmed = '')


@app.route("/", methods=['POST'])
def send_inputs():
    # take inputs from html
    ddate = request.form['ddate']
    adate = request.form['adate']
    amount = request.form['amount']
    airline = request.form['airline']
    hotel = request.form['hotel']
    result = run(amount, ddate, adate, airline, hotel)
        
    return render_template("index.html", confirmed = result)
    # handle TCP socket


if __name__ == "__main__":
    app.run(debug=True)
