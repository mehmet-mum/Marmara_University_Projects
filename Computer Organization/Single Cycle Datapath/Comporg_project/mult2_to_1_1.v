module mult2_to_1_1(out, i0,i1,s0);
output out;
input i0,i1;
input s0;
assign out = s0 ? i1:i0;
endmodule