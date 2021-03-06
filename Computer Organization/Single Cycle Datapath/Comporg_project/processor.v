module processor;
reg [31:0] pc; //32-bit prograom counter
reg clk; //clock
reg [7:0] datmem[0:31],mem[0:31]; //32-size data and instruction memory (8 bit(1 byte) for each location)
wire [31:0] 
dataa,	//Read data 1 output of Register File
datab,	//Read data 2 output of Register File
out2,		//Output of mux with ALUSrc control-mult2
out3,		//Output of mux with MemToReg control-mult3
out4,		//Output of mux with (Branch&ALUZero) control-mult4
sum,		//ALU result
extad,	//Output of sign-extend unit
adder1out,	//Output of adder which adds PC and 4-add1
adder2out,	//Output of adder which adds PC+4 and 2 shifted sign-extend result-add2
sextad,	//Output of shift left 2 unit
balrn_out,  // Output of mux with Imm_z control-mult5				//////////////////////////////////////////////
link_out,   // Output of mux with link control-mult6
addr_mem_out,	// Output of mux with ( result of sign extend or data read from memory ) b_new control-mult9
jump_addr,	// ( inst[25:0] << 2 ) + pc+4[31:28]
pc_next		// Output of mux with jump_and control-mult5		// next instr.	
;

wire [5:0] inst31_26;	//31-26 bits of instruction
wire [4:0] 
inst25_21,	//25-21 bits of instruction
inst20_16,	//20-16 bits of instruction
inst15_11,	//15-11 bits of instruction
out1;		//Write data input of Register File

wire [15:0] inst15_0;	//15-0 bits of instruction

wire [25:0] inst25_0;	//25-0 bits of instruction	////////////////////////////////////

wire [27:0] s28_out;	// output of shift left ( 26-bit )	//////////////////////////////////

wire [31:0] instruc,	//current instruction
dpack;	//Read data output of memory (data read from memory)

wire [2:0] gout;	//Output of ALU control unit

wire [1:0] flag, flag_sig,balrn_flag;	// flag control signal

wire zout,	//Zero output of ALU
const_1,	// constant_1 - bit
nout,		//Negative (sign detect) output of ALU				///////////////////////////////////
b_new1_out,	// Output of mux ( status register or zout ) with b_new ( 1 ) control-mult7
b_new2_out,	// Output of mux ( status register or 1 ) with b_new ( 2 ) 	control-mult8
jump_and,	// Output of and gate with b_new2_out and jump
reg_write_and, 	// Output of and gate with reg_write and b_succ
b_succ_out, 	// output of mux with b_succ control_mult-11
link_and,	// Output of and gate with link or ( BALRN  )
alusrc_cont,	// output of or gate with alusrc, brz
balrn,		// balrn instr.
brz,		// brz instr.
branch_cond_or, // or gate output with branch, brz, balrn 
pcsrc,	//Output of AND gate with Branch and ZeroOut inputs
end_prog,
//Control signals
regdest,alusrc,memtoreg,regwrite,memread,memwrite,branch,aluop1,aluop0,jump,b_new,link,addr_from_mem;

//32-size register file (32 bit(1 word) for each register)
reg [31:0] registerfile[0:31];

// 1-bit registers keeps status 		//////////////////////////////////////////////
reg flag_registers[0:2];

wire [31:0] const_0;			//////////////////////////////////////////////


integer i;

// datamemory connections

always @(posedge clk)
//write data to memory
if (memwrite)
begin
//sum stores address,datab stores the value to be written
datmem[sum[4:0]+3]=datab[7:0];
datmem[sum[4:0]+2]=datab[15:8];
datmem[sum[4:0]+1]=datab[23:16];
datmem[sum[4:0]]=datab[31:24];
end

//instruction memory
//4-byte instruction
 assign instruc={mem[pc[4:0]],mem[pc[4:0]+1],mem[pc[4:0]+2],mem[pc[4:0]+3]};
 assign inst31_26=instruc[31:26];
 assign inst25_21=instruc[25:21];
 assign inst20_16=instruc[20:16];
 assign inst15_11=instruc[15:11];
 assign inst15_0=instruc[15:0];
 assign inst25_0=instruc[25:0];

// registers


assign dataa=registerfile[inst25_21];//Read register 1
assign datab=registerfile[inst20_16];//Read register 2
always @(posedge clk)
begin
if( end_prog )
	$finish;
 registerfile[out1]= reg_write_and ? link_out:registerfile[out1];//Write data to register		////////////////////////////////
end
//read data from memory, sum stores address
assign dpack={datmem[sum[5:0]],datmem[sum[5:0]+1],datmem[sum[5:0]+2],datmem[sum[5:0]+3]};

assign jump_addr={adder1out[31:28],s28_out[27:0]};		/////////////////////////////////////////

assign balrn_flag = 2'b10;
assign const_1 = 1'b1;
assign const_0 = 32'b11111111111111111111111111111111;		//////////////////////////////////////////
assign balrn = (~inst31_26[5]) & (~inst31_26[4]) & (~inst31_26[3]) & (~inst31_26[2]) & (~inst31_26[1]) & (~inst31_26[0]) & (~inst15_0[5]) & inst15_0[4] & (~inst15_0[3]) & inst15_0[2] & inst15_0[1] & inst15_0[0];
assign brz = (~inst31_26[5]) & (~inst31_26[4]) & (~inst31_26[3]) & (~inst31_26[2]) & (~inst31_26[1]) & (~inst31_26[0]) & (~inst15_0[5]) & inst15_0[4] & (~inst15_0[3]) & inst15_0[2] & (~inst15_0[1]) & (~inst15_0[0]);
assign end_prog = ~|instruc;
//multiplexers
//mux with RegDst control
mult2_to_1_5  mult1(out1, inst20_16,inst15_11,regdest);

//mux with ALUSrc cont control
mult2_to_1_32 mult2(out2, datab,balrn_out,alusrc_cont);			//////////////////////////////////////////////

//mux with MemToReg control
mult2_to_1_32 mult3(out3, sum,dpack,memtoreg);

//mux with (Branch&ALUZero) control
mult2_to_1_32 mult4(out4, adder1out,addr_mem_out,pcsrc);

//mux with brz control
mult2_to_1_32 mult5(balrn_out, extad, const_0,brz);				//////////////////////////////////////////////

//mux with Link control
mult2_to_1_32 mult6(link_out, out3, adder1out,link_and);					//////////////////////////////////////////////

//mux with B_new control	( zout or flag choice )
mult2_to_1_1 mult7(b_new1_out, zout, flag_registers[flag[1:0]],b_new);			//////////////////////////////////////////////

//mux with B_new control	( 1 or flag choice )
mult2_to_1_1 mult8(b_new2_out, const_1,flag_registers[flag[1:0]],b_new);		//////////////////////////////////////////////

//mux with addr_from_mem control
mult2_to_1_32 mult9(addr_mem_out, adder2out , out3,addr_from_mem);					//////////////////////////////////////////////

//mux with jump_and control
mult2_to_1_32 mult10(pc_next, out4, jump_addr,jump_and);				//////////////////////////////////////////////

//mux with balrn control	//  condition
mult2_to_1_1 mult11(b_succ_out, const_1, flag_registers[flag[1:0]], balrn);		//////////////////////////////////////////////

//mux with balrn control	// flag choice
mult2_to_1_2 mult12(flag, flag_sig, balrn_flag,balrn);		//////////////////////////////////////////////

// load pc
always @(negedge clk)
begin
pc=pc_next;				///////////////////////////////////////////
flag_registers[0] = zout;
 flag_registers[2] = nout;
 flag_registers[1] = const_1;
end
// alu, adder and control logic connections

//ALU unit
alu32 alu1(sum,dataa,out2,zout,gout,nout);

//adder which adds PC and 4
adder add1(pc,32'h4,adder1out);

//adder which adds PC+4 and 2 shifted sign-extend result
adder add2(adder1out,sextad,adder2out);

//Control unit
control cont(instruc[31:26],regdest,alusrc,memtoreg,regwrite,memread,memwrite,branch,
aluop1,aluop0,jump,b_new,flag_sig,link,addr_from_mem);

//Sign extend unit
signext sext(instruc[15:0],extad);

//ALU control unit
alucont acont(aluop1,aluop0,instruc[3],instruc[2], instruc[1], instruc[0] ,gout);

//Shift-left 2 unit
shift shift2(sextad,extad);			/////////////////////////////////

//Shift-left 2 unit ( inst[25:0] )
shift_26 shift_26(s28_out,inst25_0);			/////////////////////////////////

//AND gate
assign pcsrc=branch_cond_or & b_new1_out;
assign jump_and= jump & b_new2_out;			////////////////////////////////// 
assign reg_write_and= b_succ_out & regwrite & ~(brz);


//OR gate 		////////////////
assign alusrc_cont= alusrc|brz|balrn;
assign link_and= link | balrn;
assign branch_cond_or= branch | balrn | brz;


//initialize datamemory,instruction memory and registers
//read initial data from files given in hex
initial
begin
$readmemh("initDm.dat",datmem); //read Data Memory
$readmemh("initIM.dat",mem);//read Instruction Memory
$readmemh("initReg.dat",registerfile);//read Register File

	for(i=0; i<31; i=i+1)
	$display("Instruction Memory[%0d]= %h  ",i,mem[i],"Data Memory[%0d]= %h   ",i,datmem[i],
	"Register[%0d]= %h",i,registerfile[i]);
end

initial
begin
pc=-4;
#400 $finish;





end
initial
begin
clk=0;
//40 time unit for each cycle
forever #20  clk=~clk;
end
initial 
begin
  $monitor($time,"PC %h",pc,"  SUM %h",sum,"   INST %h",instruc[31:0],
"   REGISTER %h %h %h %h ",registerfile[4],registerfile[5], registerfile[6],registerfile[1] );
end
endmodule

