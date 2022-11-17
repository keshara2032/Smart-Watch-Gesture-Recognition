var udp = require('dgram');
const createCsvWriter = require('csv-writer').createObjectCsvWriter;

const csvWriter = createCsvWriter({
  path: 'smart_watch_data.csv',
  header: [
    {id: 'acc_x', title: 'Accelerometer X Axis'},
    {id: 'acc_y', title: 'Accelerometer Y Axis'},
    {id: 'acc_z', title: 'Accelerometer Z Axis'}
  ]
});

// --------------------creating a udp server --------------------

// creating a udp server
var server = udp.createSocket('udp4');

// emits when any error occurs
server.on('error',function(error){
  console.log('Error: ' + error);
  server.close();
});

// emits on new datagram msg
server.on('message',function(msg,info){
  console.log('Data received from client : ' + msg.toString());
  console.log('Received %d bytes from %s:%d\n',msg.length, info.address, info.port);
  raw_values = msg.toString().split(',')

  const data = [
    {
      acc_x: raw_values[0],
      acc_y: raw_values[1],
      acc_z: raw_values[2]
    }
  ];
  
  csvWriter
  .writeRecords(data)
  .then(()=> console.log('The CSV file was written successfully'));  

//sending msg
server.send(msg,info.port,'localhost',function(error){
  if(error){
    client.close();
  }else{
    console.log('Data sent !!!');
  }

});

});

//emits when socket is ready and listening for datagram msgs
server.on('listening',function(){
  var address = server.address();
  var port = address.port;
  var family = address.family;
  var ipaddr = address.address;
  console.log('Server is listening at port' + port);
  console.log('Server ip :' + ipaddr);
  console.log('Server is IP4/IP6 : ' + family);
});

//emits after the socket is closed using socket.close();
server.on('close',function(){
  console.log('Socket is closed !');
});

server.bind(7889);

// setTimeout(function(){
// server.close();
// },8000);
