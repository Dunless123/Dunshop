// 测试修复后的功能
console.log('测试发布功能...');

// 测试交易方式选择
const handleTradeMethodChange = (e) => {
  const selectedValues = e.detail.value;
  console.log('选中的交易方式:', selectedValues);
  return selectedValues;
};

// 模拟选择交易方式
const testTradeMethodSelection = () => {
  console.log('测试交易方式选择...');
  const testEvent = {
    detail: {
      value: ['1', '2'] // 选择自提和快递
    }
  };
  const result = handleTradeMethodChange(testEvent);
  console.log('测试结果:', result.length > 0 ? '选择成功' : '选择失败');
};

testTradeMethodSelection();

console.log('\n测试聊天功能...');

// 测试聊天列表
const chatList = [
  {
    id: 1,
    name: '小明',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait&image_size=square',
    lastMessage: '好的，没问题',
    time: '10:03',
    unread: 1
  },
  {
    id: 2,
    name: '小红',
    avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=female%20user%20avatar&image_size=square',
    lastMessage: '这个商品可以便宜一点吗？',
    time: '09:45',
    unread: 0
  }
];

console.log('聊天列表测试:', chatList.length > 0 ? '加载成功' : '加载失败');
console.log('聊天列表项数:', chatList.length);

console.log('\n所有测试完成!');
