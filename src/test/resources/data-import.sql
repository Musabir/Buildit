insert into plant_inventory_entry (id, name, description, price) values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);
insert into plant_inventory_entry (id, name, description, price) values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);
insert into plant_inventory_entry (id, name, description, price) values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);
insert into plant_inventory_entry (id, name, description, price) values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);
insert into plant_inventory_entry (id, name, description, price) values (5, 'Maxi excavator', '15 Tonne Large excavator', 400);
insert into plant_inventory_entry (id, name, description, price) values (6, 'Maxi excavator', '20 Tonne Large excavator', 450);
insert into plant_inventory_entry (id, name, description, price) values (7, 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);
insert into plant_inventory_entry (id, name, description, price) values (8, 'FT dumper', '2 Tonne Front Tip Dumper', 180);
insert into plant_inventory_entry (id, name, description, price) values (9, 'FT dumper', '2 Tonne Front Tip Dumper', 200);
insert into plant_inventory_entry (id, name, description, price) values (10, 'FT dumper', '2 Tonne Front Tip Dumper', 300);
insert into plant_inventory_entry (id, name, description, price) values (11, 'FT dumper', '3 Tonne Front Tip Dumper', 400);
insert into plant_inventory_entry (id, name, description, price) values (12, 'Loader', 'Hewden Backhoe Loader', 200);
insert into plant_inventory_entry (id, name, description, price) values (13, 'D-Truck', '15 Tonne Articulating Dump Truck', 250);
insert into plant_inventory_entry (id, name, description, price) values (14, 'D-Truck', '30 Tonne Articulating Dump Truck', 300);

insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (1, 1, 'A01', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (2, 2, 'A02', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (3, 3, 'A03', 'UNSERVICEABLE_REPAIRABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (4, 4, 'A04', 'UNSERVICEABLE_INCOMPLETE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (5, 5, 'A05', 'UNSERVICEABLE_CONDEMNED');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (6, 6, 'A06', 'UNSERVICEABLE_REPAIRABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (7, 6, 'A07', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (8, 6, 'A08', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (9, 9, 'A09', 'SERVICEABLE');
insert into plant_inventory_item (id, plant_info_id, serial_number, equipment_condition) values (10, 10, 'A09', 'SERVICEABLE');


insert into purchase_order (id, issue_date, payment_schedule, total, status) values (1, '2017-02-10', '2017-03-24', 300, 'CLOSED');
insert into purchase_order (id, issue_date, payment_schedule, total, status) values (2, '2017-03-23', '2017-03-25', 300, 'CLOSED');


insert into plant_reservation (id, plant_id, start_date, end_date) values (1, 1, '2017-02-10', '2017-03-24');
insert into plant_reservation (id, plant_id, start_date, end_date) values (2, 8, '2017-03-23', '2017-03-25');

--maintenance related reservation
--strict version
insert into plant_reservation (id, plant_id, start_date, end_date) values (3, 9, '2017-03-22', '2017-03-25');
--relaxed version (query from date 2017-04-16)
insert into plant_reservation (id, plant_id, start_date, end_date) values (4, 10, '2017-04-08', '2017-04-15');

--corrective repairs per year for the last 5 years
insert into plant_reservation (id, plant_id, start_date, end_date) values (5, 1, '2013-04-08', '2013-04-15');
insert into plant_reservation (id, plant_id, start_date, end_date) values (6, 2, '2013-03-08', '2013-03-15');
insert into plant_reservation (id, plant_id, start_date, end_date) values (7, 3, '2013-02-08', '2013-02-15'); --belongs to different maintenance plan
insert into plant_reservation (id, plant_id, start_date, end_date) values (8, 4, '2014-04-08', '2014-04-15');
insert into plant_reservation (id, plant_id, start_date, end_date) values (9, 5, '2015-04-08', '2015-04-15');
insert into plant_reservation (id, plant_id, start_date, end_date) values (10, 6, '2015-03-08', '2015-03-15');


insert into maintenance_plan (id, year_of_action) values (1, 2017);
insert into maintenance_plan (id, year_of_action) values (2, 2013);
insert into maintenance_plan (id, year_of_action) values (3, 2013);
insert into maintenance_plan (id, year_of_action) values (4, 2014);
insert into maintenance_plan (id, year_of_action) values (5, 2015);


--strict, relaxed maintenance
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (1, 1, 3, 'lorem ipsum 1', 'PREVENTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (2, 1, 4, 'lorem ipsum 2', 'OPERATIVE', 100);

--corrective repairs per year for the last 5 years
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (3, 2, 5, 'lorem ipsum 3', 'CORRECTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (4, 2, 6, 'lorem ipsum 4', 'CORRECTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (5, 3, 7, 'lorem ipsum 5', 'CORRECTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (6, 4, 8, 'lorem ipsum 6', 'CORRECTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (7, 5, 9, 'lorem ipsum 7', 'CORRECTIVE', 100);
insert into maintenance_task (id, maint_plan_id, reservation_id, description, type_of_work, price) values (8, 5, 10, 'lorem ipsum 8', 'CORRECTIVE', 100);