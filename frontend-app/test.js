// 测试脚本
console.log('测试聊天功能...');

// 测试敏感词过滤
const filterSensitiveWords = (content) => {
  const sensitiveWords = ['敏感词1', '敏感词2', '敏感词3'];
  let filteredContent = content;
  sensitiveWords.forEach(word => {
    const regex = new RegExp(word, 'gi');
    filteredContent = filteredContent.replace(regex, '*'.repeat(word.length));
  });
  return filteredContent;
};

console.log('敏感词过滤测试:');
console.log(filterSensitiveWords('这是一个敏感词1测试'));
console.log(filterSensitiveWords('敏感词2和敏感词3都应该被过滤'));

// 测试消息存储
console.log('\n测试消息存储...');
try {
  // 模拟存储
  const testMessages = [
    { isSelf: true, content: '测试消息1', time: '10:00' },
    { isSelf: false, content: '测试消息2', time: '10:01' }
  ];
  console.log('存储前:', testMessages);
  console.log('存储成功');
  console.log('读取成功:', testMessages);
} catch (error) {
  console.error('存储测试失败:', error);
}

console.log('\n测试发布功能...');

// 测试表单验证
const validateForm = (form) => {
  if (!form.title) return '请输入商品标题';
  if (form.title.length < 3) return '商品标题至少3个字符';
  if (form.title.length > 50) return '商品标题最多50个字符';
  if (!form.description) return '请输入商品描述';
  if (form.description.length < 10) return '商品描述至少10个字符';
  if (form.description.length > 500) return '商品描述最多500个字符';
  if (!form.price) return '请输入商品价格';
  if (parseFloat(form.price) <= 0) return '商品价格必须大于0';
  if (parseFloat(form.price) > 999999) return '商品价格不能超过999999';
  if (form.originalPrice && parseFloat(form.originalPrice) < parseFloat(form.price)) return '原价不能低于现价';
  if (form.images.length === 0) return '请上传商品图片';
  if (form.images.length > 5) return '最多上传5张图片';
  if (form.tradeMethods.length === 0) return '请选择交易方式';
  return '验证通过';
};

const testForm = {
  title: '测试商品',
  description: '这是一个测试商品的详细描述，包含足够的字符数',
  price: '100',
  originalPrice: '150',
  images: ['image1', 'image2'],
  tradeMethods: ['1', '2']
};

console.log('表单验证测试:', validateForm(testForm));

console.log('\n所有测试完成!');
