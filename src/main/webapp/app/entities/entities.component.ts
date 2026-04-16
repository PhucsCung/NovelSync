import { defineComponent, provide } from 'vue';

import CategoryService from './category/category.service';
import AuthorService from './author/author.service';
import BookService from './book/book.service';
import InventoryReceiptService from './inventory-receipt/inventory-receipt.service';
import ReceiptDetailService from './receipt-detail/receipt-detail.service';
import CustomerService from './customer/customer.service';
import OrderService from './order/order.service';
import OrderDetailService from './order-detail/order-detail.service';
import EmployeeService from './employee/employee.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('categoryService', () => new CategoryService());
    provide('authorService', () => new AuthorService());
    provide('bookService', () => new BookService());
    provide('inventoryReceiptService', () => new InventoryReceiptService());
    provide('receiptDetailService', () => new ReceiptDetailService());
    provide('customerService', () => new CustomerService());
    provide('orderService', () => new OrderService());
    provide('orderDetailService', () => new OrderDetailService());
    provide('employeeService', () => new EmployeeService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
