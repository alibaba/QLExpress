address = {
  'owner': 'cole',
  age: 30,
  contacts: [
    {
      name: 'cassandra',
      phoneNumber: '0000000'
    },
    {
      name: 'cole',
      phoneNumber: '1111111'
    }
  ]
};
assert(address['owner'] == 'cole');
assert(address['age'] == 30);
assert(address.contacts[0].phoneNumber == '0000000');

List addressBook = [address, {owner: 'john'}];

assert(addressBook[0].owner == 'cole');
assert(addressBook[1].owner == 'john');

empty = {:};
assert(empty.a == null);

extra_comma_map = {
    "test_id" : "acd",
    "cc_id"   : "ttt",
}
assert(extra_comma_map.test_id == "acd")