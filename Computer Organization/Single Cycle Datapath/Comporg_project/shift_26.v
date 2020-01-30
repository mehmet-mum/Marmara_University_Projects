module shift_26(shout,shin);
output [27:0] shout;
input [25:0] shin;
assign shout=({{ 2 {shin[25]}}, shin})<<2;
endmodule