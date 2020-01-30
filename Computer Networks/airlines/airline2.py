import sqlite3
from sqlite3 import Error
from http.server import BaseHTTPRequestHandler, HTTPServer


HOST = '127.0.0.1'
PORT = 8060

MAX_NUM = 25

number, departure, arrival, hotel_name = 0, '', '', ''

# create connection with database
def create_connection(db_file):
    try:
        conn = sqlite3.connect(db_file)
    except Error as e:
        print(e)
    return conn

database = "./airline2.db"
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
    

# get available seats from database
def get_number(cur, date, type, hotel_name):
    cur.execute("SELECT seat FROM flight where date = '" + date + "' and type like '" + type + "' and place like '" + hotel_name + "'")
    rows = cur.fetchall()    
    return rows

# insert data to database
def insert_data(cur, date, type, number, hotel_name):
    values = "'" +date+ "', '"+ type + "', " + str(MAX_NUM-number) + ", '" + hotel_name + "')"
    cur.execute('''INSERT INTO flight (date, type, seat, place) 
                    VALUES(''' + values)
    conn.commit()

# update data in database
def update_data(cur, date, type, number, hotel_name):
    sql_query = "UPDATE flight SET seat = " + str(number) + " WHERE date = '" + date + "' and type like '" + type + "' and place like '" + hotel_name + "'" 
    cur.execute(sql_query)
    conn.commit()

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
            global hotel_name
            number = int(data[1])
            departure = data[2]
            arrival = data[3]
            hotel_name = data[4]

            arrival_bool, departure_bool = False, False

            #   send code 200 response  
            self.send_response(200)  

            #send header first  
            self.send_header('Content-type','get-response')  
            self.end_headers() 

            global departure_number
            global departure_status

            global arrival_number
            global arrival_status

            # get available seats for arrival date from database
            rows = get_number(cur, arrival, 'a', hotel_name)

            # if all seats is empty
            if len(rows) == 0:
                if number <= MAX_NUM:
                    arrival_status = 'insert'
                    arrival_bool = True

            # if all seats is not empty, consider remaining seats
            else:
                arrival_number = int(rows[0][0])
                if arrival_number > number:
                    arrival_status = 'update'
                    arrival_bool = True

            # get available seats for departure date from database
            rows = get_number(cur, departure, 'd', hotel_name)

            # if all seats is empty
            if len(rows) == 0:
                if number <= MAX_NUM:
                    departure_status = 'insert'
                    departure_bool = True
                    
            # if all seats is not empty, consider remaining seats
            else:
                departure_number = int(rows[0][0])
                if departure_number > number:
                    departure_status = 'update'
                    departure_bool = True            
            
            if arrival_bool and departure_bool:
                self.wfile.write("OK".encode())
            else:
                self.wfile.write("NOT_OK".encode())
                    
        elif message_type == 'update_database':
            if arrival_status == 'insert':
                insert_data(cur, arrival, "a", number, hotel_name)
            else:  
                update_data(cur, arrival, "a", arrival_number - number, hotel_name)

            if departure_status == 'insert':
                insert_data(cur, departure, "d", number, hotel_name)
            else:
                update_data(cur, departure, "d", departure_number - number, hotel_name)


            sql_query = "Select * from flight"
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
    sql_create_table = """ CREATE TABLE IF NOT EXISTS flight (
                                        date date NOT NULL,
                                        type char(1) NOT NULL,
                                        seat integer NOT NULL,
                                        place char(20) NOT NULL
                                    ); """
    
    
    # create tables
    if conn is not None:
        create_table(conn, sql_create_table)
        run()
    else:
        print("Error! cannot create the database connection.")
