#Bingham Zheng
#SQL Query Task on SQLite DB
import sqlite3 as lite
import csv
import re
import pandas as pd
import argparse
import collections
import json
import glob
import math
import os
import requests
import string
import sqlite3
import sys
import time
import xml


class Movie_db(object):
    def __init__(self, db_name):
        #db_name: "cs1656-public.db"
        self.con = lite.connect(db_name)
        self.cur = self.con.cursor()
    
    #q0 is an example 
    def q0(self):
        query = '''SELECT COUNT(*) FROM Actors'''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q1(self):
        query = '''
            DROP VIEW IF EXISTS Eighty
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW Eighty as
            SELECT c.aid as aid, m.mid
            FROM Movies as m, Cast as c
            WHERE c.mid = m.mid AND m.year >= 1980 AND m.year < 1990
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS TwoThousand
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW TwoThousand as
            SELECT c.aid as aid, m.mid
            FROM Cast as c, Movies as m
            WHERE c.mid = m.mid AND m.year >= 2000
            '''
        self.cur.execute(query)

        query = '''
            SELECT fname, lname
            FROM Actors as a, Eighty, TwoThousand
            WHERE a.aid = TwoThousand.aid AND a.aid = Eighty.aid
            ORDER BY a.fname, a.lname
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows
        
    def q2(self):
        query = '''
            SELECT Movies.title, Movies.year
            FROM Movies
            WHERE Movies.year = (SELECT year FROM Movies WHERE title = 'Rogue One: A Star Wars Story') AND
                Movies.rank > (SELECT rank FROM Movies WHERE title = 'Rogue One: A Star Wars Story')
            ORDER BY Movies.title
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q3(self):
        query = '''
            DROP VIEW IF EXISTS sw
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW sw as
                SELECT a.fname, a.lname, m.title
                FROM Actors as a, Cast as c, Movies as m
                WHERE a.aid = c.aid AND c.mid = m.mid AND title LIKE '%Star Wars%'
            '''
        self.cur.execute(query)

        query = '''
            SELECT fname, lname, COUNT(DISTINCT title) as count
            FROM sw
            GROUP BY fname, lname
            ORDER BY count DESC, lname, fname
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q4(self):
        query = '''
            DROP VIEW IF EXISTS after
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW after as
                SELECT c.aid
                FROM Actors as a, Cast as c, Movies as m
                WHERE m.mid IN (SELECT mid
                                FROM Movies as m
                                WHERE m.year >= 1980) AND m.mid = c.mid AND c.aid = a.aid
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS before
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW before as
                SELECT c.aid, a.fname, a.lname
                FROM Actors as a, Cast as c, Movies as m
                WHERE m.mid IN (SELECT mid
                                FROM Movies as m
                                WHERE m.year < 1980) AND m.mid = c.mid AND c.aid = a.aid
            '''
        self.cur.execute(query)

        query = '''
            SELECT DISTINCT a.fname, a.lname
            FROM  Actors as a, before as b
            WHERE b.aid NOT IN after AND a.aid = b.aid
            ORDER BY a.lname, a.fname
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q5(self):
        query = '''
            SELECT d.fname, d.lname, COUNT(*) as count
            FROM Directors as d, Movie_Director as m
            WHERE d.did = m.did
            GROUP BY d.fname, d.lname
            ORDER BY count DESC, d.lname ASC, d.fname ASC
            LIMIT 10
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q6(self):
        query = '''
            DROP VIEW IF EXISTS castnum
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW castnum as
                SELECT m.mid, COUNT(DISTINCT c.aid) as count
                FROM Cast as c, Movies as m
                WHERE c.mid = m.mid
                GROUP BY m.mid
                ORDER BY count desc
                LIMIT 10
            '''
        self.cur.execute(query)

        query = '''
            SELECT m.title, COUNT(c.aid) as count
            FROM Cast as c, Movies as m
            WHERE c.mid = m.mid
            GROUP BY m.mid
            HAVING count >= (SELECT MIN(count) FROM castnum)
            ORDER BY count DESC
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q7(self):
        query = '''
            DROP VIEW IF EXISTS f
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW f as
                SELECT DISTINCT c.mid, COUNT(a.gender) as female
                FROM Actors as a, Cast as c
                WHERE c.aid = a.aid AND a.gender = 'Female'
                GROUP BY c.mid
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS m
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW m as
                SELECT DISTINCT c.mid, COUNT(a.gender) as male
                FROM Actors as a, Cast as c
                WHERE c.aid = a.aid AND a.gender = 'Male'
                GROUP BY c.mid
            '''
        self.cur.execute(query)

        query = '''
            SELECT mo.title, f.female, m.male
            FROM Movies as mo, f, m
            WHERE mo.mid = m.mid AND m.mid = f.mid AND m.male < f.female
            ORDER BY mo.title
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q8(self):
        query = '''
            SELECT a.fname, a.lname, count(distinct d.did) as direct
            FROM Actors as a, Cast as c, Directors as d, Movies as m, Movie_Director as md
            WHERE a.aid = c.aid AND c.mid = md.mid AND md.did = d.did AND a.fname != d.fname AND a.lname != d.lname
            GROUP BY a.fname, a.lname
            HAVING direct >= 7
            ORDER BY direct desc
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q9(self):
        query = '''
            DROP VIEW IF EXISTS start
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW start as
            SELECT a.aid, MIN(year) as startYear
            FROM Actors as a, Cast as c, Movies as m
            WHERE m.mid = c.mid AND a.aid = c.aid
            GROUP By a.aid
            '''
        self.cur.execute(query)

        query = '''
            SELECT fname, lname, COUNT(*)
            FROM Actors as a, Cast as c, Movies as m
            WHERE m.mid = c.mid AND a.aid = c.aid
            AND m.year = (SELECT startYear
                          FROM start as s
                          WHERE s.aid = a.aid)
            AND a.fname LIKE 'D%'
            GROUP BY a.fname, a.lname
            ORDER BY COUNT(*) DESC
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q10(self):
        query = '''
            SELECT a.lname, m.title
            FROM Actors as a, Cast as c, Directors as d, Movies as m, Movie_Director as md
            WHERE a.aid = c.aid AND c.mid = m.mid AND m.mid = md.mid AND md.did = d.did
                AND a.lname = d.lname
            ORDER BY a.lname, m.title
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q11(self):
        query = '''
            DROP VIEW IF EXISTS bacon
            '''
        self.cur.execute(query)
        query = '''
            CREATE VIEW bacon as
                SELECT c.mid as mid
                FROM Cast as C
                NATURAL JOIN Actors as a
                WHERE a.fname = 'Kevin' AND a.lname = 'Bacon'
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS bacon1
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW bacon1 as
                SELECT c.mid as mid, a.aid as aid
                FROM Actors as a, bacon
                NATURAL JOIN Cast as c
                WHERE c.mid IN (bacon.mid)
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS bacon2
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW bacon2 as
                SELECT a.aid as aid, c.mid as mid
                FROM Actors as a
                JOIN bacon1 as b1
                ON a.aid = b1.aid
                JOIN cast as c
                ON a.aid = c.aid
                GROUP BY a.aid, c.mid
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS bacon3
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW bacon3 as
                SELECT a.aid as aid, c.mid as mid
                FROM Actors as a
                JOIN Cast as c
                ON a.aid = c.aid
                JOIN bacon2
                ON bacon2.mid = c.mid
                WHERE c.mid IN (bacon2.mid)
                GROUP BY a.aid
            '''
        self.cur.execute(query)

        query = '''
            DROP VIEW IF EXISTS finalbacon
            '''
        self.cur.execute(query)

        query = '''
            CREATE VIEW finalbacon as
                SELECT aid, mid
                FROM bacon3
                EXCEPT
                SELECT aid, mid
                FROM bacon2
                GROUP BY aid
            '''
        self.cur.execute(query)

        query = '''
            SELECT a.fname, a.lname
            FROM finalbacon, Actors as a
            WHERE a.aid = finalbacon.aid
            GROUP BY a.lname, a.fname
            ORDER BY a.lname, a.fname
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

    def q12(self):
        query = '''
            SELECT a.fname, a.lname, count(*), AVG(m.rank) as pop
            FROM Actors as a, Cast as c, Movies as m
            WHERE a.aid = c.aid AND c.mid = m.mid
            GROUP BY a.fname, a.lname
            ORDER BY pop desc
            LIMIT 20
            '''
        self.cur.execute(query)
        all_rows = self.cur.fetchall()
        return all_rows

if __name__ == "__main__":
    task = Movie_db("cs1656-public.db")
    rows = task.q0()
    print(rows)
    print()
    rows = task.q1()
    print(rows)
    print()
    rows = task.q2()
    print(rows)
    print()
    rows = task.q3()
    print(rows)
    print()
    rows = task.q4()
    print(rows)
    print()
    rows = task.q5()
    print(rows)
    print()
    rows = task.q6()
    print(rows)
    print()
    rows = task.q7()
    print(rows)
    print()
    rows = task.q8()
    print(rows)
    print()
    rows = task.q9()
    print(rows)
    print()
    rows = task.q10()
    print(rows)
    print()
    rows = task.q11()
    print(rows)
    print()
    rows = task.q12()
    print(rows)
    print()
