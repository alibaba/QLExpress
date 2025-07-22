// tag::classifiedJson[]
myHome = {
  '@class': 'com.alibaba.qlexpress4.inport.MyHome',
  'sofa': 'a-sofa',
  'chair': 'b-chair',
  'myDesk': {
    'book1': 'Then Moon and Sixpence',
    '@class': 'com.alibaba.qlexpress4.inport.MyDesk'
  },
  // ignore field that don't exist
  'notexist': 1234
}
assert(myHome.getSofa()=='a-sofa')
assert(myHome instanceof com.alibaba.qlexpress4.inport.MyHome)
assert(myHome.getMyDesk().getBook1()=='Then Moon and Sixpence')
assert(myHome.getMyDesk() instanceof com.alibaba.qlexpress4.inport.MyDesk)
// end::classifiedJson[]

// @class override
myDesk = {
  '@class': 'com.alibaba.qlexpress4.inport.MyHome',
  'book1': 'Then Moon and Sixpence',
  '@class': 'com.alibaba.qlexpress4.inport.MyDesk'
}
assert(myDesk instanceof com.alibaba.qlexpress4.inport.MyDesk)

// cls not exist
m = {
  '@class': 'notexist',
  'book1': 'Then Moon and Sixpence'
}
assert(m["@class"]=='notexist')
assert(m instanceof Map)

// invalid assignment
assertErrorCode(() -> {
  '@class': 'com.alibaba.qlexpress4.inport.MyHome',
  'sofa': 'a-sofa',
  'chair': 'b-chair',
  'bed': 'c-bed'
}, "INVALID_ASSIGNMENT")

