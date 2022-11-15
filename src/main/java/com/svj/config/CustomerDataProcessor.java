package com.svj.config;

import com.svj.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerDataProcessor implements ItemProcessor<Customer, Customer> {


    @Override
    public Customer process(Customer customer) throws Exception {
        int age= Integer.parseInt(customer.getAge());
        if(age>= 18){
            return customer;
        }
        return null;
    }


}
