import { Authority } from '@/shared/security/authority';
const Entities = () => import('@/entities/entities.vue');

const Category = () => import('@/entities/category/category.vue');
const CategoryUpdate = () => import('@/entities/category/category-update.vue');
const CategoryDetails = () => import('@/entities/category/category-details.vue');

const Author = () => import('@/entities/author/author.vue');
const AuthorUpdate = () => import('@/entities/author/author-update.vue');
const AuthorDetails = () => import('@/entities/author/author-details.vue');

const Book = () => import('@/entities/book/book.vue');
const BookUpdate = () => import('@/entities/book/book-update.vue');
const BookDetails = () => import('@/entities/book/book-details.vue');

const InventoryReceipt = () => import('@/entities/inventory-receipt/inventory-receipt.vue');
const InventoryReceiptUpdate = () => import('@/entities/inventory-receipt/inventory-receipt-update.vue');
const InventoryReceiptDetails = () => import('@/entities/inventory-receipt/inventory-receipt-details.vue');

const ReceiptDetail = () => import('@/entities/receipt-detail/receipt-detail.vue');
const ReceiptDetailUpdate = () => import('@/entities/receipt-detail/receipt-detail-update.vue');
const ReceiptDetailDetails = () => import('@/entities/receipt-detail/receipt-detail-details.vue');

const Customer = () => import('@/entities/customer/customer.vue');
const CustomerUpdate = () => import('@/entities/customer/customer-update.vue');
const CustomerDetails = () => import('@/entities/customer/customer-details.vue');

const Order = () => import('@/entities/order/order.vue');
const OrderUpdate = () => import('@/entities/order/order-update.vue');
const OrderDetails = () => import('@/entities/order/order-details.vue');

const OrderDetail = () => import('@/entities/order-detail/order-detail.vue');
const OrderDetailUpdate = () => import('@/entities/order-detail/order-detail-update.vue');
const OrderDetailDetails = () => import('@/entities/order-detail/order-detail-details.vue');

const Employee = () => import('@/entities/employee/employee.vue');
const EmployeeUpdate = () => import('@/entities/employee/employee-update.vue');
const EmployeeDetails = () => import('@/entities/employee/employee-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'category',
      name: 'Category',
      component: Category,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/new',
      name: 'CategoryCreate',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/edit',
      name: 'CategoryEdit',
      component: CategoryUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'category/:categoryId/view',
      name: 'CategoryView',
      component: CategoryDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author',
      name: 'Author',
      component: Author,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/new',
      name: 'AuthorCreate',
      component: AuthorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/:authorId/edit',
      name: 'AuthorEdit',
      component: AuthorUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'author/:authorId/view',
      name: 'AuthorView',
      component: AuthorDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book',
      name: 'Book',
      component: Book,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/new',
      name: 'BookCreate',
      component: BookUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/:bookId/edit',
      name: 'BookEdit',
      component: BookUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'book/:bookId/view',
      name: 'BookView',
      component: BookDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-receipt',
      name: 'InventoryReceipt',
      component: InventoryReceipt,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-receipt/new',
      name: 'InventoryReceiptCreate',
      component: InventoryReceiptUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-receipt/:inventoryReceiptId/edit',
      name: 'InventoryReceiptEdit',
      component: InventoryReceiptUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'inventory-receipt/:inventoryReceiptId/view',
      name: 'InventoryReceiptView',
      component: InventoryReceiptDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'receipt-detail',
      name: 'ReceiptDetail',
      component: ReceiptDetail,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'receipt-detail/new',
      name: 'ReceiptDetailCreate',
      component: ReceiptDetailUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'receipt-detail/:receiptDetailId/edit',
      name: 'ReceiptDetailEdit',
      component: ReceiptDetailUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'receipt-detail/:receiptDetailId/view',
      name: 'ReceiptDetailView',
      component: ReceiptDetailDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer',
      name: 'Customer',
      component: Customer,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/new',
      name: 'CustomerCreate',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/edit',
      name: 'CustomerEdit',
      component: CustomerUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'customer/:customerId/view',
      name: 'CustomerView',
      component: CustomerDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order',
      name: 'Order',
      component: Order,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/new',
      name: 'OrderCreate',
      component: OrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/:orderId/edit',
      name: 'OrderEdit',
      component: OrderUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order/:orderId/view',
      name: 'OrderView',
      component: OrderDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-detail',
      name: 'OrderDetail',
      component: OrderDetail,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-detail/new',
      name: 'OrderDetailCreate',
      component: OrderDetailUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-detail/:orderDetailId/edit',
      name: 'OrderDetailEdit',
      component: OrderDetailUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'order-detail/:orderDetailId/view',
      name: 'OrderDetailView',
      component: OrderDetailDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee',
      name: 'Employee',
      component: Employee,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/new',
      name: 'EmployeeCreate',
      component: EmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/:employeeId/edit',
      name: 'EmployeeEdit',
      component: EmployeeUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'employee/:employeeId/view',
      name: 'EmployeeView',
      component: EmployeeDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
