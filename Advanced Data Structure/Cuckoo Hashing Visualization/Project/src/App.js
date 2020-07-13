import React, { Component } from 'react';
import './App.css';

class App extends Component {
  state = {
    num_of_tables: 0,
    table_size: 0,
    number_of_collision: 0
  }


  // prepare select tags  for number of cells and number of tables
  add_numbers_to_selector = (select_id, min_num, max_num) =>{
    var selector = document.getElementById(select_id);
    selector.innerHTML = "";

    for( min_num; min_num<=max_num; min_num++){
      selector.options[selector.options.length] = new Option(min_num);
    }
  }


  // create tables
  create_tables = () =>{
    document.getElementById("user_inputs").className = "passive";
    document.getElementById("cuckoo_hashing").className = "active"

    // take number of tables and table size
    var number_of_tables = document.getElementById("select_1").value;
    var number_of_cells = document.getElementById("select_2").value;

    this.setState(
      {
        num_of_tables: number_of_tables,
        table_size: number_of_cells
      }
    )

    for(number_of_tables; number_of_tables>0; number_of_tables--){
      this.add_cells_to_tables("table_" + number_of_tables, number_of_cells);
    }

    // prepare indexes
    this.add_indexes(number_of_cells);

  }
  
  componentDidMount(){

    // min value 2 and max value 5 for number of tables
    this.add_numbers_to_selector("select_1", 2, 5);

    // min value 10 and max value 30 for table size
    this.add_numbers_to_selector("select_2", 10, 30);
  }

  // prepare tables
  add_cells_to_tables = (table_id, number_of_cells) =>{
    var table = document.getElementById(table_id);
    table.className = "active";
    for( number_of_cells; number_of_cells > 0; number_of_cells--){
      table.appendChild(document.createElement("p"));
    }
  }

  // prepare indexes
  add_indexes = (number) =>{
    var indexes = document.getElementById("indexes");
    indexes.className = "index";
    for(let i = 0; i < number; i++){
      var p_tag = document.createElement("p");
      p_tag.innerHTML = i;
      indexes.appendChild(p_tag);
    }
  }
  
  // hash function 1 for table 1
  hash_function_1 = (key, table_size) =>{
    return key % table_size
  }

  // hash function 2 for table 2
  hash_function_2 = (key, table_size) =>{
    var value = key;
    var sum = 0;
    while (value) {
      sum += value % 10;
      value = Math.floor(value / 10);
    }
    return ((sum * key) + key) % table_size
  }

  // hash function 3 for table 3
  hash_function_3 = (key, table_size) =>{
    var value = key;
    var sum = 0;
    var i = 23;
    while (value) {
      sum += (value % 10) * i;
      value = Math.floor(value / 10);
      i++;
    }
    return (sum) % table_size
  }

  // hash function 4 for table 4
  hash_function_4 = (key, table_size) =>{
    return (key ** 2) % table_size
  }

  // hash function 5 for table 5
  hash_function_5 = (key, table_size) =>{
    var value = parseInt(key.toString().split('').reverse().join(''));
    return value % table_size
  }

  // change info message
  change_message = (message, color) =>{
    var message_tag = document.getElementById("message");
    message_tag.innerHTML = message;
    message_tag.className = color;
  }

  // clear info message and collision number after changing input text
  handle_change = () =>{
    this.change_message("Ready for hashing :)", "green");
    this.setState({
      number_of_collision: 0
    })
    document.getElementById("log").innerHTML = "";
  }

  // handle button click
  handle_click = (e) =>{
    // get button id
    var butt = e.target.id;
    // get input
    var input_number = document.getElementById("input_number").value;

    // if input is empty show error
    if(input_number === ""){
      this.change_message("Input is empty!", "red");
    }
    else{
      var table_index;

      // perform insert operation
      if (butt === "insert"){
        // search input if exist or not
        table_index = this.search(input_number);
        
        // if not exist try to insert
        if ( table_index === -1){
          
          this.insert(input_number, 1, 0, false, input_number,"");
        }

        // if exist show error message
        else{
          this.change_message("Insertion is failed. " + input_number + " is already in tables!", "red");
        }
      }

      // perform delete operation
      else if (butt === "delete"){
        this.delete(input_number);
      }

      // perform search operation
      else if (butt === "search"){
        table_index = this.search(input_number);
        var hash_index = this.get_key_index(input_number, table_index);

        // if input not found show error message
        if(hash_index === -1){
          this.change_message("Search is failed. " + input_number + " is not found!", "red");
        }

        // if input found show info message
        else{
          this.change_message(input_number + " is found at index " + hash_index + " of table " + (table_index), "green");
        }
      }
    }
  }

  // convert text to sum of ascii codes of each char 
  text_to_ascii = (text) =>{
    var sum = 0;
    for(let i = text.length - 1; i>-1 ; i--){
      sum = sum + text.charCodeAt(i);
    }

    return sum;
  }

  // calculate load factor for table
  calculate_load_factor(table_index){
    
    var table_children = document.getElementById("table_" + table_index).children;
    var length = parseInt(this.state.table_size) + 2;
    var full_cells = 0;
    
    for ( var i = 2; i < length; i++){
      if( table_children[i].innerHTML === ""){
        full_cells++;
      }
    }
    full_cells = length - 2 - full_cells;
    
    return (Math.round((full_cells / (length-2)) * 100) / 100).toFixed(2);
  }

  // get index of a input
  get_key_index = (key, table_index) =>{

    // conver input to key
    key = this.text_to_ascii(key);

    var hash_index = -1;

    //calculate index for given table
    if(table_index === 1){
      hash_index = this.hash_function_1(key, this.state.table_size);
    }
    else if(table_index === 2){
      hash_index = this.hash_function_2(key, this.state.table_size);
    }
    else if(table_index === 3){
      hash_index = this.hash_function_3(key, this.state.table_size);
    }
    else if(table_index === 4){
      hash_index = this.hash_function_4(key, this.state.table_size);
    }
    else if(table_index === 5){
      hash_index = this.hash_function_5(key, this.state.table_size);
    }

    return hash_index
  }
  

  // insert operation
  insert = (key, table_index, colli, check_loop, the_inserting_number, log) =>{


    // check for loop
    if( check_loop ){

      // if loop occured
      if(the_inserting_number == key && table_index === 1){

        this.change_message("Insertion failed!", "red");

        this.setState({
          number_of_collision: colli
        });
        return;
      }
    }

    // calculate index of input for given table
    var hash_index = this.get_key_index(key, table_index);
    
    var table_children = document.getElementById("table_" + table_index).children;
    var cell_value = table_children[hash_index + 2].innerHTML;

    // if the cell is empty just insert
    if(cell_value === ""){
      this.change_message("Insertion is successfull!" ,"green");
      this.setState({
        number_of_collision: colli
      });
      
      table_children[hash_index + 2].innerHTML = key;
      table_children[0].innerHTML = "Load Factor: " + this.calculate_load_factor(table_index);

      // update log
      log = log + "<p>--->  " + "'<span>" + key + "</span>' is inserted to " + hash_index + ". index of table " + table_index + "</p>";

      document.getElementById("log").innerHTML = "<h2> Table Operations</h2>" + log;
    }

    // if not empty insert input to the table and take old value and insert old value nex table
    else{
      table_children[hash_index + 2].innerHTML = key;
      log = log + "<p>--->  " + "'<span>" + key + "</span>' is inserted to " + hash_index + ". index of table " + table_index + " and '<span>" + cell_value + "</span>' is taken from the table.</p>";
      if(table_index === parseInt(this.state.num_of_tables)){
        table_index = 1;
      }
      else{
        table_index++;
      }

      colli++;
      this.insert(cell_value, table_index, colli, true, the_inserting_number, log);
    }
  }

  // delete operation
  delete = (key) =>{

    // search input in tables
    for( let i = 1; i <= this.state.num_of_tables; i++){
      var hash_index = this.get_key_index(key, i);

      var table_children = document.getElementById("table_" + i).children;
      var cell_value = table_children[hash_index + 2].innerHTML;

      // if found delete it 
      if( cell_value === key){

        table_children[hash_index + 2].innerHTML = "";
        table_children[0].innerHTML = "Load Factor: " + this.calculate_load_factor(i);
        this.change_message("Deletion is successfull!", "green");
        return;
      }

    }
    // if not found show error message
    this.change_message("Deletion is failed! " + key + " is not found!", "red");
  }


  // search operation
  search = (key) =>{
    // search input in tables
    for( let i = 1; i <= this.state.num_of_tables; i++){
      var hash_index = this.get_key_index(key, i);
      var table_children = document.getElementById("table_" + i).children;
      var cell_value = table_children[hash_index + 2].innerHTML;

      // if found return index
      if( cell_value === key){
        return i;
      }

    }
    // if not found return -1
    return -1;
    
  }
  

  // frontend part
  render(){
    return(
      <div>
        <div id="user_inputs" className="active">
          <h2>Please select number of tables and number of cells in the tables</h2>
          <div>
            <p>Number of tables:</p>
            <select id="select_1"></select>
          </div>
          
          <div>
            <p>Number of cells:</p>
            <select id="select_2"></select>
          </div>
          <input type="submit" value="Create Tables" onClick={this.create_tables}></input>
        </div>

        <div id="cuckoo_hashing" className="passive">
          <div id="hash_operations">
            <h1>Cuckoo Hashing</h1>
            <div>
              <p>Input: </p>
              <input id="input_number" type="text" maxLength="20" onChange={this.handle_change}></input>
            </div>
            <input id="insert" className="button" type="submit" value="Insert" onClick={this.handle_click}></input>
            <input id="delete" className="button" type="submit" value="Delete" onClick={this.handle_click}></input>
            <input id="search" className="button" type="submit" value="Search" onClick={this.handle_click}></input>
            <h2 id="message" className="green">Ready for hashing :)</h2>
          </div>
          <div id="tables">
            <div id="indexes" className="passive">
              <h4>Collisions: {this.state.number_of_collision}</h4>
              <h3>indexes</h3>
            </div>
            <div id="table_1" className="passive">
              <h4>Load Factor: 0.00</h4>
              <h3>Table 1</h3>
            </div>
            <div id="table_2" className="passive">
              <h4>Load Factor: 0.00</h4>
              <h3>Table 2</h3>
            </div>
            <div id="table_3" className="passive">
              <h4>Load Factor: 0.00</h4>
              <h3>Table 3</h3>
            </div>
            <div id="table_4" className="passive">
              <h4>Load Factor: 0.00</h4>
              <h3>Table 4</h3>
            </div>
            <div id="table_5" className="passive">
              <h4>Load Factor: 0.00</h4>
              <h3>Table 5</h3>
            </div>
          </div>
          <div id="log"></div>
        </div>
      </div>
    )
  }
}

export default App;
