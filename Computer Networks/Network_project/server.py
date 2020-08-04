#!/usr/bin/env python3
import http.client 
import socket

HOST = '127.0.0.1'  # Standard loopback interface address (localhost)
PORT = 65430      # Port to listen on (non-privileged ports are > 1023)

PORTOBELLO_PORT = 8090
VEGAS_PORT = 8080

PEGASUS_PORT = 8060
THY_PORT = 8070
# open TCP socket and wait
def run():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        try:
            s.bind((HOST, PORT))
        except OSError:
            print("Connection can't be esablished")
            return 1
        listen(s)
        
            
def listen(s):
    try:
        s.listen()
        print('Port is listening at', PORT)
        conn, addr = s.accept()
        session = handleConnection(conn, addr)
        if (session == 'Connection closed'):
            listen(s)
    except KeyboardInterrupt:
        pass
    finally:
        s.close()


# this part is for second suggestion
def get_another_hotel(departure_date, arrival_date, number_of_passenger, hotel_name, airline_name):
    if hotel_name == 'vegas':
        http_conn_hotel = http.client.HTTPConnection(HOST, VEGAS_PORT)
        http_conn_hotel.request('GET', 'get_info&' + number_of_passenger + '&' + 
            departure_date + '&' + arrival_date)
    else:
        http_conn_hotel = http.client.HTTPConnection(HOST, PORTOBELLO_PORT)
        http_conn_hotel.request('GET', 'get_info&' + number_of_passenger + '&' + 
            departure_date + '&' + arrival_date)
                    
    if airline_name == 'thy':
        http_conn_airline = http.client.HTTPConnection(HOST, THY_PORT)
        http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
            departure_date + '&' + arrival_date + '&' + hotel_name)
    else:
        http_conn_airline = http.client.HTTPConnection(HOST, PEGASUS_PORT)
        http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
            departure_date + '&' + arrival_date + '&' + hotel_name)


    hotel_respond = http_conn_hotel.getresponse().read().decode()
    airline_respond = http_conn_airline.getresponse().read().decode()
    http_conn_airline.close()
    http_conn_hotel.close()

    # return responds
    return hotel_respond, airline_respond

def handleConnection(conn, addr):
    with conn:
        try:
            print('Connection established by', addr)
            # make while true loop here
            while True:
                # take data comes from TCP connection
                data = conn.recv(1024).decode()
                print(data)
                #sptlit data
                resp = data.split('&')
                number_of_passenger = resp[0]
                departure_date = resp[1]
                arrival_date = resp[2]
                airline_name = resp[3]
                hotel_name = resp[4]


                #create HTTP connections
                if hotel_name == 'vegas':
                    http_conn_hotel = http.client.HTTPConnection(HOST, VEGAS_PORT)
                    http_conn_hotel.request('GET', 'get_info&' + number_of_passenger + '&' + 
                        departure_date + '&' + arrival_date)
                else:
                    http_conn_hotel = http.client.HTTPConnection(HOST, PORTOBELLO_PORT)
                    http_conn_hotel.request('GET', 'get_info&' + number_of_passenger + '&' + 
                        departure_date + '&' + arrival_date)
                    
                if airline_name == 'thy':
                    http_conn_airline = http.client.HTTPConnection(HOST, THY_PORT)
                    http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
                        departure_date + '&' + arrival_date + '&' + hotel_name)
                else:
                    http_conn_airline = http.client.HTTPConnection(HOST, PEGASUS_PORT)
                    http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
                        departure_date + '&' + arrival_date + '&' + hotel_name)


                # get respond messages
                hotel_respond = http_conn_hotel.getresponse().read().decode()
                print('Hotel ', hotel_respond)

                airline_respond = http_conn_airline.getresponse().read().decode()
                print('Airline ', airline_respond)

            
                # if both respons message are 'OK' then complete reservation
                if hotel_respond == 'OK' and airline_respond == 'OK':
                    http_conn_hotel.request('GET', 'update_database')
                    http_conn_airline.request('GET', 'update_database')
                    send_back_response(conn, addr, 'Your Resevation Succesfully Completed')

                else:
                    situation = 'NOT_OK'
                    #first suggestion
                    if hotel_respond == 'OK' and airline_respond == 'NOT_OK':
                        if airline_name == 'thy':
                            http_conn_airline = http.client.HTTPConnection(HOST, PEGASUS_PORT)
                            http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
                                departure_date + '&' + arrival_date + '&' + hotel_name)
                            rez_respond = 'THY is not available, you may use PEGASUS'
                        else:
                            http_conn_airline = http.client.HTTPConnection(HOST, THY_PORT)
                            http_conn_airline.request('GET', 'get_info&' + number_of_passenger + '&' + 
                                departure_date + '&' + arrival_date + '&' + hotel_name)
                            rez_respond = 'PEGASUS is not available, you mat use THY'

                        situation = http_conn_airline.getresponse().read().decode()
                        http_conn_airline.close()
                        if situation == 'OK':
                            send_back_response(conn, addr, rez_respond)

                    #second suggestion 
                    if situation == 'NOT_OK':
                        if( hotel_name == 'portobello'):
                            hotel_respond, airline_respond = get_another_hotel(departure_date, arrival_date, number_of_passenger, 'vegas', 'thy')

                            if hotel_respond == 'OK' and airline_respond == 'NOT_OK':
                                hotel_respond, airline_respond = get_another_hotel(departure_date, arrival_date, number_of_passenger, 'vegas', 'pegasus')
                                if hotel_respond == 'OK' and airline_respond == 'OK':
                                    send_back_response(conn, addr, 'Given hotel is not available, You may use vegas and pegasus')
                            elif hotel_respond == 'OK' and airline_respond == 'OK':
                                send_back_response(conn, addr, 'Given hotel is not available, You may use vegas and thy')
                        elif( hotel_name == 'vegas'):
                            hotel_respond, airline_respond = get_another_hotel(departure_date, arrival_date, number_of_passenger, 'portobello', 'thy')

                            if hotel_respond == 'OK' and airline_respond == 'NOT_OK':
                                hotel_respond, airline_respond = get_another_hotel(departure_date, arrival_date, number_of_passenger, 'portobello', 'pegasus')
                                if hotel_respond == 'OK' and airline_respond == 'OK':
                                    send_back_response(conn, addr, 'Given hotel is not available, You may use portobello and pegasus')
                            elif hotel_respond == 'OK' and airline_respond == 'OK':
                                send_back_response(conn, addr, 'Given hotel is not available, You may use portobello and thy')
                
                send_back_response(conn, addr, 'Given date is not available, please choose another date')

        except KeyboardInterrupt:
            conn.close()
        except IndexError:
            conn.close()
            return 'Connection closed'
        except OSError:
            conn.close()
            return 'Connection closed'

def send_back_response(conn, addr, resp):
    with conn:
        conn.send(resp.encode())
    

if __name__ == "__main__":
    run()
