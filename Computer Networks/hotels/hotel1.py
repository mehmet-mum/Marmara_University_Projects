import sqlite3
from sqlite3 import Error
from http.server import BaseHTTPRequestHandler, HTTPServer
from datetime import datetime, timedelta


HOST = '127.0.0.1'
PORT = 8090

MAX_NUM = 60

number, departure, arrival = 0, '', ''

# create connection with database
def create_connection(db_file):
    try:
        conn = sqlite3.connect(db_file)
    except Error as e:
        print(e)
    return conn

database = "./hotel1.db"
conn = create_connection(database)

departure_status = ''
arrival_status = ''

arrival_number = 0
departure_number = 0

#create table if necessary
def create_table(conn, create_table_sql):
    
    try:
        c = conn.cursor()
        c.execute(create_table_sql)
    except Error as e:
        print(e)
    

# get available rooms from database
def get_number(cur, date):
    cur.execute("SELECT seat FROM room where date = '" + date + "'")
    rows = cur.fetchall()    
    return rows

# insert data to database
def insert_data(cur, date, number):
    values = "'" +date+ "', " + str(MAX_NUM-number) + ")"
    cur.execute('''INSERT INTO room (date, seat) 
                    VALUES(''' + values)
    conn.commit()

# update data in database
def update_data(cur, date, number):
    sql_query = "UPDATE room SET seat = " + str(number) + " WHERE date = '" + date + "'" 
    cur.execute(sql_query)
    conn.commit()

#check for availabilty for given dates
def get_availabilty(cur, departure, arrival, number):
    departure_date = datetime.strptime(departure, "%Y-%m-%d")
    arrival_date = datetime.strptime(arrival, "%Y-%m-%d")
    while departure_date <= arrival_date:
        date = str(departure_date).split(' ')
        rows = get_number(cur, date[0])

        if len(rows) == 0:
            if number > MAX_NUM:
                return False
        elif number > rows[0][0]:
                return False

        departure_date = departure_date + timedelta(days = 1)
        
    return True

# complete reservation
def insert_rezervation(cur, departure, arrival, number):
    departure_date = datetime.strptime(departure, "%Y-%m-%d")
    arrival_date = datetime.strptime(arrival, "%Y-%m-%d")

    while departure_date <= arrival_date:
        date = str(departure_date).split(' ')
        rows = get_number(cur, date[0])

        if len(rows) == 0 :
            insert_data(cur, date[0], number)
        else:
            update_data(cur, date[0], rows[0][0] - number)
        
        departure_date = departure_date + timedelta(days = 1)

# get message handler      
class HTTPRequestHandler(BaseHTTPRequestHandler):  
    def do_GET(self):
        data = self.path.split('&')
        message_type = data[0]
        cur = conn.cursor()
        if message_type == 'get_info' :
            global number
            global departure
            global arrival
            number = int(data[1])
            departure = data[2]
            arrival = data[3]

            

            #   send code 200 response  
            self.send_response(200)  

            #send header first  
            self.send_header('Content-type','get-response')  
            self.end_headers() 

    
            # get available seats for arrival date from database
            availability = get_availabilty(cur, departure, arrival, number)

            
            if availability:
                self.wfile.write("OK".encode())
            else:
                self.wfile.write("NOT_OK".encode())


        elif message_type == 'update_database':
            
            insert_rezervation(cur, departure, arrival, number)

            sql_query = "Select * from room"
            rows = conn.execute(sql_query)

            for row in rows:
                print (row)
        


def run():  
  print('http server is starting...')  
  
  #ip and port of server  
  #by default http server port is 80  
  server_address = (HOST, PORT)  
  httpd = HTTPServer(server_address, HTTPRequestHandler)  
  print('http server is running...')  
  httpd.serve_forever()  

if __name__ == '__main__':
    sql_create_table = """ CREATE TABLE IF NOT EXISTS room (
                                        date date NOT NULL,
                                        seat integer NOT NULL
                                    ); """
    
    
    # create tables
    if conn is not None:
        create_table(conn, sql_create_table)
        run()
    else:
        print("Error! cannot create the database connection.")
