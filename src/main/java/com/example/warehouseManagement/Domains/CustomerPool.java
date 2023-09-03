package com.example.warehouseManagement.Domains;

import java.util.List;

public class CustomerPool {
    private CustomerPool() {
    }

    public static List<Customer> customerList = List.of(
        
    Customer.builder().name("Acme Corporation").street("123 Main Street").city(" Anytown").state("CA").zipcode(91234).contactInfo("(555) 123-4567").build(),
    Customer.builder().name("Apple Inc.").street("1 Infinite Loop").city(" Cupertino").state("CA").zipcode(95014).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("Google LLC").street("1600 Amphitheatre Parkway").city(" Mountain View").state("CA").zipcode(94043).contactInfo("(555) 345-6789").build(),
    Customer.builder().name("Microsoft Corporation").street("1 Microsoft Way").city(" Redmond").state("WA").zipcode(98052).contactInfo("(555) 456-7890").build(),
    Customer.builder().name("Amazon.com, Inc.").street("410 Terry Ave N").city(" Seattle").state("WA").zipcode(98109).contactInfo("(555) 567-8901").build(),
    Customer.builder().name("Facebook, Inc.").street("1 Hacker Way").city(" Menlo Park").state("CA").zipcode(94025).contactInfo("(555) 678-9012").build(),
    Customer.builder().name("Tesla, Inc.").street("3500 Deer Creek Road").city(" Palo Alto").state("CA").zipcode(94304).contactInfo("(555) 789-0123").build(),
    Customer.builder().name("SpaceX").street("1 Rocket Road").city(" Hawthorne").state("CA").zipcode(90250).contactInfo("(555) 890-1234").build(),
    Customer.builder().name("Uber Technologies, Inc.").street("1455 Market St").city(" San Francisco").state("CA").zipcode(94103).contactInfo("(555) 901-2345").build(),
    Customer.builder().name("BigCorp, Inc.").street("123 Fake Street").city(" Anytown").state("CA").zipcode(91234).contactInfo("(555) 012-3456").build(),
    Customer.builder().name("SmallCo, LLC").street("456 Real Street").city(" Springfield").state("IL").zipcode(62701).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("CoolCompany").street("789 Awesome Street").city(" New York").state("NY").zipcode(10001).contactInfo("(555) 345-6789").build(),
    Customer.builder().name("GreatDeals, Inc.").street("1011 Best Street").city(" Chicago").state("IL").zipcode(60601).contactInfo("(555) 456-7890").build(),
    Customer.builder().name("AmazingStuff, LLC").street("1212 Great Street").city(" San Francisco").state("CA").zipcode(94107).contactInfo("(555) 567-8901").build(),
    Customer.builder().name("TopNotch, Inc.").street("1313 Awesome Street").city(" Philadelphia").state("PA").zipcode(19107).contactInfo("(555) 678-9012").build(),
    Customer.builder().name("BestPrices, LLC").street("1414 Great Street").city(" Houston").state("TX").zipcode(77002).contactInfo("(555) 789-0123").build(),
    Customer.builder().name("IncredibleDeals, Inc.").street("1515 Awesome Street").city(" Dallas").state("TX").zipcode(75201).contactInfo("(555) 890-1234").build(),
    Customer.builder().name("FantasticStuff, LLC").street("1616 Great Street").city(" Phoenix").state("AZ").zipcode(85004).contactInfo("(555) 901-2345").build(),
    Customer.builder().name("Outstanding, Inc.").street("1717 Awesome Street").city(" Seattle").state("WA").zipcode(98101).contactInfo("(555) 012-3456").build(),
    Customer.builder().name("SuperCompany").street("1818 Best Street").city(" Portland").state("OR").zipcode(97201).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("MegaCorp").street("1919 Awesome Street").city(" Denver").state("CO").zipcode(80202).contactInfo("(555) 012-3456").build(),
    Customer.builder().name("GiantCo, LLC").street("2020 Great Street").city(" Salt Lake City").state("UT").zipcode(84101).contactInfo("(555) 123-4567").build(),
    Customer.builder().name("HugeDeals, Inc.").street("2121 Awesome Street").city(" Las Vegas").state("NV").zipcode(89101).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("ColossalStuff, LLC").street("2222 Great Street").city(" Los Angeles").state("CA").zipcode(90001).contactInfo("(555) 345-6789").build(),
    Customer.builder().name("EnormousPrices, Inc.").street("2323 Awesome Street").city(" Miami").state("FL").zipcode(33101).contactInfo("(555) 456-7890").build(),
    Customer.builder().name("IBM").street("1100 Main Street").city(" Armonk").state("NY").zipcode(10504).contactInfo("(555) 567-8901").build(),
    Customer.builder().name("Berkshire Hathaway").street("3555 Farnam Street").city(" Omaha").state("NE").zipcode(68131).contactInfo("(555) 678-9012").build(),
    Customer.builder().name("JPMorgan Chase & Co.").street("270 Park Avenue").city(" New York").state("NY").zipcode(10017).contactInfo("(555) 789-0123").build(),
    Customer.builder().name("Wells Fargo & Company").street("420 Montgomery Street").city(" San Francisco").state("CA").zipcode(94104).contactInfo("(555) 890-1234").build(),
    Customer.builder().name("Bank of America").street("100 North Tryon Street").city(" Charlotte").state("NC").zipcode(28255).contactInfo("(555) 901-2345").build(),
    Customer.builder().name("Alphabet Inc.").street("1600 Amphitheatre Parkway").city(" Mountain View").state("CA").zipcode(94043).contactInfo("(555) 012-3456").build(),
    Customer.builder().name("Johnson & Johnson").street("1 Johnson & Johnson Plaza").city(" New Brunswick").state("NJ").zipcode(8933).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("Procter & Gamble").street("1 Procter & Gamble Plaza").city(" Cincinnati").state("OH").zipcode(45202).contactInfo("(555) 345-6789").build(),
    Customer.builder().name("Aardvark Software ").street(" 123 Main Street").city(" Anytown").state("CA").zipcode(91234).contactInfo("(555) 456-7890").build(),
    Customer.builder().name("Banana Stand ").street(" 456 Elm Street").city(" Springfield").state("IL").zipcode(62701).contactInfo("(555) 567-8901").build(),
    Customer.builder().name("Caterpillar Inc. ").street(" 789 Maple Street").city(" New York").state("NY").zipcode(10001).contactInfo("(555) 678-9012").build(),
    Customer.builder().name("Dog House ").street(" 1011 Oak Street").city(" Chicago").state("IL").zipcode(60601).contactInfo("(555) 789-0123").build(),
    Customer.builder().name("Elephant Insurance ").street(" 1212 Pine Street").city(" San Francisco").state("CA").zipcode(94107).contactInfo("(555) 890-1234").build(),
    Customer.builder().name("Fish & Chips ").street(" 1313 Walnut Street").city(" Philadelphia").state("PA").zipcode(19107).contactInfo("(555) 901-2345").build(),
    Customer.builder().name("Giraffe Safari ").street(" 1414 Elm Street").city(" Houston").state("TX").zipcode(77002).contactInfo("(555) 012-3456").build(),
    Customer.builder().name("Horseback Riding ").street(" 1515 Maple Street").city(" Dallas").state("TX").zipcode(75201).contactInfo("(555) 234-5678").build(),
    Customer.builder().name("Iguana Rescue ").street(" 1616 Oak Street").city(" Phoenix").state("AZ").zipcode(85004).contactInfo("(555) 345-6789").build(),
    Customer.builder().name("Jellyfish Aquarium ").street(" 1717 Pine Street").city(" Seattle").state("WA").zipcode(98101).contactInfo("(555) 456-7890").build(),
    Customer.builder().name("Kangaroo Farm ").street(" 1818 Best Street").city(" Portland").state("OR").zipcode(97201).contactInfo("(555) 567-8901").build(),
    Customer.builder().name("Lion Safari ").street(" 1919 Awesome Street").city(" Denver").state("CO").zipcode(80202).contactInfo("(555) 678-9012").build(),
    Customer.builder().name("Monkey Business ").street(" 2020 Great Street").city(" Salt Lake City").state("UT").zipcode(84101).contactInfo("(555) 789-0123").build(),
    Customer.builder().name("Ostrich Farm ").street(" 2121 Awesome Street").city(" Las Vegas").state("NV").zipcode(89101).contactInfo("(555) 890-1234").build(),
    Customer.builder().name("Peacock Feathers ").street(" 2222 Great Street").city(" Los Angeles").state("CA").zipcode(90001).contactInfo("(555) 901-2345").build(),
    Customer.builder().name("Quack Quack ").street(" 2323 Awesome Street").city(" Miami").state("FL").zipcode(33101).contactInfo("(555) 012-3456").build()
    );

}
